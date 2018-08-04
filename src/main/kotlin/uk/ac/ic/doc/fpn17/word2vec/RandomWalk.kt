package uk.ac.ic.doc.fpn17.word2vec

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator
import org.deeplearning4j.text.sentenceiterator.SentenceIterator
import org.deeplearning4j.text.sentenceiterator.StreamLineIterator
import org.nd4j.linalg.io.ClassPathResource
import uk.ac.ic.doc.fpn17.equivalences.*
import uk.ac.ic.doc.fpn17.logic.FOLFormula
import uk.ac.ic.doc.fpn17.logic.False
import uk.ac.ic.doc.fpn17.logic.Formula
import java.util.*
import kotlin.streams.toList
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory
import org.deeplearning4j.models.word2vec.Word2Vec
import sun.print.CUPSPrinter.getPort
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.ui.api.UIServer
import org.deeplearning4j.plot.BarnesHutTsne
import org.nd4j.linalg.api.ndarray.INDArray
import org.deeplearning4j.models.word2vec.wordstore.VocabCache
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable
import org.deeplearning4j.ui.stats.StatsListener
import org.deeplearning4j.ui.storage.InMemoryStatsStorage
import org.nd4j.linalg.api.buffer.DataBuffer
import java.util.ArrayList
import org.nd4j.linalg.factory.Nd4j
import uk.ac.ic.doc.fpn17.logic.True
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


/**
 * copy pasta https://stackoverflow.com/questions/47850156/get-a-random-item-from-list-using-kotlin-streams
 * Returns a random element using the specified [random] instance as the source of randomness.
 */
fun <E> List<E>.random(random: java.util.Random): E? = if (size > 0) get(random.nextInt(size)) else null

val equivalencesAvailable: List<Equivalence> = arrayOf(OrAssociativityReverse(), OrAssociativity(), OrIntroductionFalseVariant(), OrIntroductionTrueVariant1(), AndAssociativityReverse(), AndAssociativity(), AndContradiction(), AndFalse1(), AndFalse2(), ReverseAndTrue1(), ReverseAndTrue2(), ReverseAAndA(), AndTrue1(), AndTrue2(), AAndA(), CommutativityAnd(), CommutativityOr()).asList()

fun doWalks(start: FOLFormula = False(), numWalks: Int = (1024 * 1024), lengthWalks: Int = 16): Array<Array<String>> {
    val word = getNextWord()
    wordIndex[start] = word
    wordReverseIndex[word] = start
    return (0 until numWalks).toList().parallelStream().map { _ ->
        doWalk(start, lengthWalks)
    }.toList().toTypedArray()
}

fun doWalk(start: FOLFormula, lengthWalks: Int = 16, random: Random = Random()): Array<String> {
    fun selectMatchingEquivalence(formula: FOLFormula): Equivalence {
        val candidate = equivalencesAvailable.random(random)!!
        if (candidate.matches(formula) > 0) {
            return candidate;
        }
        return selectMatchingEquivalence(formula);
    }

    fun selectMatchToOperateOn(formula: FOLFormula, equivalence: Equivalence): Int {
        val matches = equivalence.matches(formula);
        return random.ints(0, matches).findFirst().asInt;
    }

    fun nextFormula(formula: FOLFormula): FOLFormula {
        val equivalence = selectMatchingEquivalence(formula)
        return equivalence.apply(formula, selectMatchToOperateOn(formula, equivalence))
    }

    fun formulaToWord(formula: FOLFormula): String {
        if (wordIndex.contains(formula))
            return wordIndex[formula]!!
        else {
            val word = getNextWord()
            wordIndex[formula] = word
            wordReverseIndex[word] = formula
            return word;
        }
    }

    var current = start;
    val res = (0 until lengthWalks).map {
        val prev = current
        current = nextFormula(current);
        formulaToWord(prev)
    }.toTypedArray()
    return res;
}

fun main(args: Array<String>) {
    wordIndex.clear()
    wordReverseIndex.clear()
    try {
        val walks1 = doWalks(start = False(),numWalks = 2 * 1024)
        val walks2 = doWalks(start = True(),numWalks = 2 * 1024)
        val walks = walks1 + walks2
        val sentenceify = walks.map { it.joinToString(" ") }
        val iter: SentenceIterator = CollectionSentenceIterator(sentenceify)
        val t = DefaultTokenizerFactory()
        t.tokenPreProcessor = CommonPreprocessor()
        println("Building model....")
        val vec = Word2Vec.Builder()
                .minWordFrequency(5)
                .layerSize(128)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build()

        val server = UIServer.getInstance()
        val statsStorage = InMemoryStatsStorage()
        server.attach(statsStorage)
        System.out.println("Started on port " + server.getPort())

        println("Fitting Word2Vec model....")
        vec.fit()

        println("Closest Words:")
        val lst = vec.wordsNearest("Worda", 10)
        println(lst)
        println("started writing")
        WordVectorSerializer.writeWordVectors(vec, "target/classes/pathToWriteto.txt")
        println("written")
        Nd4j.setDataType(DataBuffer.Type.DOUBLE)
        val cacheList = ArrayList<String>() //cacheList is a dynamic array of strings used to hold all words

        //STEP 2: Turn text input into a list of words
        println("Load & Vectorize data....")
        val wordFile = ClassPathResource("/pathToWriteto.txt").file   //Open the file
        //Get the data of all unique word vectors
        val vectors = WordVectorSerializer.loadTxt(wordFile)
        val cache = vectors.second
        val weights = vectors.first.syn0    //seperate weights of unique words into their own list

        for (i in 0 until cache.numWords())
        //seperate strings of words into their own list
            cacheList.add(cache.wordAtIndex(i))

        val numDim = 3;

        //STEP 3: build a dual-tree tsne to use later
        println("Build model....")
        val tsne = BarnesHutTsne.Builder()
                .setMaxIter(1).theta(0.5)
                .normalize(false)
                .learningRate(500.0)
                .useAdaGrad(false).numDimension(numDim)
                //                .usePca(false)
                .build()

        //STEP 4: establish the tsne values and save them to a file
        println("Store TSNE Coordinates for Plotting....")
        val outputFile = "target/archive-tmp/tsne-standard-coords.csv." + numDim.toString()
        File(outputFile).getParentFile().mkdirs()

        tsne.fit(weights)
        println("tsne fitted")
        tsne.saveAsFile(cacheList, outputFile)
        println("file saved")
        rewriteCsv(outputFile,numDim)
        println("csv renamed")
    } finally {
        wordReverseIndex.close()
        wordIndex.close()
        System.exit(0)
    }
}

fun rewriteCsv(path: String,numDim : Int) {
    val reader = Files.newBufferedReader(Paths.get(path));
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT);
    val writer = Files.newBufferedWriter(Paths.get(path + ".v2"));
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT);

    for (csvRecord in csvParser) {
        // Accessing Values by Column Index
        val word = csvRecord[numDim].subSequence(0,csvRecord[numDim].length - 1);
        val formulaString = wordReverseIndex[word]!!.toPrefixNotation()
        if(numDim == 2){
            csvPrinter.printRecord(csvRecord[0],csvRecord[1],formulaString)
        }else{

            csvPrinter.printRecord(csvRecord[0],csvRecord[1],csvRecord[2],formulaString)
        }
    }
    writer.close()
}