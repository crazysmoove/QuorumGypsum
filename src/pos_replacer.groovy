import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap

def projectRoot = 'c:/projects/words/QuorumGypsum/'

def refFile = "${projectRoot}/reference/mpos/mobyposi.txt"
BiMap<String, List<String>> posToWordListBiMap = HashBiMap.create()
def posToWordListMap = [:].withDefault { [] }

new File(refFile).eachLine { line ->

    def (word, posString) = line.split(/\uFFFD+/)
    def posCode = String.valueOf(posString.charAt(0))

    posToWordListMap[posCode] << word

    List<String> wordsForPos = posToWordListBiMap.get(posCode)
    if (!wordsForPos) {
        wordsForPos = new ArrayList<String>()
        posToWordListBiMap.put(posCode, wordsForPos)
    }
    wordsForPos.add(word)

}

BiMap<List<String>, String> wordListToPosBiMap = posToWordListBiMap.inverse()

//println posToWordListBiMap.get("!")
//posToWordListMap.each { k, v -> if (k == "N") { println "${k}: ${v}" } }

def testFile = "${projectRoot}/corpus/test-sample-1.txt"

new File(testFile).eachLine { line ->

    if (line =~ /^\s*\/\//) {
        return // skip comments
    }

    //println line

    if (line =~ /^\s*$/) {
        return
    }

    line = line.replaceAll(/[^A-Za-z0-9 ]/, '')
    line = line.toLowerCase()

    List<String> words = line.split(/\b/).grep { String w -> w.matches(/\w+/) } as List<String>

    /*List<String> partsOfSpeech = words.collect { String word ->
        def key = wordListToPosBiMap.keySet().grep({ it.contains(word) })
        wordListToPosBiMap.get(key)
    }*/
    List<String> replacements = []
    Random random = new Random()
    for (String word in words) {
        def wordLists = posToWordListMap.values()
        def matchingList = wordLists.find { it.contains(word) }
        if (!matchingList) {
            replacements << word
        } else {
            //def matchingPos = posToWordListMap.find{ it.value == matchingList }.key
            def randomWord = matchingList[random.nextInt(matchingList.size() + 1)]
            replacements << randomWord
        }
    }

    println replacements.join(' ')

}
