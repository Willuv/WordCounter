import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * A program that asks for a file and outputs a table of the words and counts
 * listed in alphabetical order.
 *
 * @author William Uvlin
 *
 */
public final class WordCounter {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private WordCounter() {
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int firstPos = position;
        int secondPos = position;
        boolean flag = true;

        if (separators.contains(text.charAt(firstPos))) {
            while (secondPos < text.length() && flag) {
                if (separators.contains(text.charAt(secondPos))) {
                    secondPos++;
                } else {
                    flag = false;
                }
            }
        } else {
            while (secondPos < text.length() && flag) {
                if (!separators.contains(text.charAt(secondPos))) {
                    secondPos++;
                } else {
                    flag = false;
                }
            }
        }
        return text.substring(firstPos, secondPos);
    }

    /**
     *
     * @param in
     *            The file that the map will be filled from
     * @return A map with all unique words and how many times they appear in the
     *         text
     * @requires [The file is not empty]
     * @ensures [A map containing all unique words and number of appearances]
     */
    private static Map<String, Integer> generateMap(SimpleReader in) {
        //Create a map and several stacks to hold the terms and definitions
        Map<String, Integer> words = new Map1L<String, Integer>();
        //set of separator characters
        Set<Character> sep = new Set1L<Character>();
        sep.add('.');
        sep.add('-');
        sep.add(',');
        sep.add(' ');
        sep.add('/');
        sep.add('\'');
        sep.add(';');
        sep.add(':');
        sep.add('!');
        sep.add('?');
        sep.add('\t');
        String currentWord = "";
        int val = 0;
        int i = 0;

        //Create a string for each line
        String line = in.nextLine();

        //loop while not at the end of the stream
        while (!in.atEOS()) {
            //if your'e at a blank line and its not at the end
            //go to the next line
            if (line.isEmpty() && !in.atEOS()) {
                line = in.nextLine();
            }
            while (i < line.length()) {
                currentWord = nextWordOrSeparator(line, i, sep);
                if (!sep.contains(currentWord.charAt(0))) {
                    if (!words.hasKey(currentWord)) {
                        words.add(currentWord, 1);
                    } else {
                        val = words.value(currentWord);
                        words.remove(currentWord);
                        words.add(currentWord, val + 1);
                    }
                }
                i += currentWord.length();
            }
            line = in.nextLine();
            i = 0;
        }
        //return the map
        return words;
    }

    /**
     * Takes the map of words and number of appearances and sorts them in
     * alphabetical order.
     *
     * @param mapToSort
     *            the map that needs sorting
     *
     * @return Queue with the words in alphabetical order
     *
     * @requires [Map is not empty]
     * @ensures [Returned Queue with the words in alphabetical order]
     */
    private static Queue<String> abcSort(Map<String, Integer> mapToSort) {
        //create a Queue that will hold the terms
        Queue<String> que = new Queue1L<>();

        //for loop to get each key (term) in the map and add it to que
        for (Map.Pair<String, Integer> pair : mapToSort) {
            que.enqueue(pair.key());
        }

        //sorts the Queue in alphabetical order
        que.sort(String.CASE_INSENSITIVE_ORDER);
        //return the Queue
        return que;
    }

    /**
     * Takes the map of words and number of appearances and sorts them in
     * alphabetical order.
     *
     * @param out
     *            the file being written
     * @param wordsAndAmt
     *            the map with all words and counts
     * @param order
     *            the queue of words in alphabetical order
     *
     * @requires [wordsAndAmt and order are not empty and that the file]
     * @ensures [A file with all words and counts in alphabetical order]
     */
    private static void generatePage(SimpleWriter out,
            Map<String, Integer> wordsAndAmt, Queue<String> order) {

        //current word string to hold the word when its dequeued from the queue
        String current = "";

        //print the opening header to the file
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Words Counted in data/gettysburg.txt</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Words Counted in data/gettysburg.txt</h2>");

        //create the top of the table
        out.println("<table border=\"1\">");
        out.println("<tbody>");
        out.println("<tr>");
        out.println("<th>Words</th>");
        out.println("<th>Counts</th>");
        out.println("</tr>");

        //while loop to go through order and wordsAndAmt to fill the table
        while (order.length() > 0) {
            current = order.dequeue();
            out.println("<tr>");
            out.println("<th>" + current + "</th>");
            out.println("<th>" + wordsAndAmt.value(current) + "</th>");
            out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        Map<String, Integer> wordsAndNum = new Map1L<String, Integer>();
        Queue<String> wordList = new Queue1L<>();

        //take the input and make a SimpleReader for the file
        out.print("Enter the file to be read: ");
        SimpleReader inputFile = new SimpleReader1L(in.nextLine());

        //ask for the location where the glossary should be placed
        out.print("Enter the desired location of the index: ");
        String output = in.nextLine();

        //create a SimpleWriter for this new file
        SimpleWriter outputFile = new SimpleWriter1L(output + "/index.html");

        wordsAndNum.transferFrom(generateMap(inputFile));
        wordList.transferFrom(abcSort(wordsAndNum));
        generatePage(outputFile, wordsAndNum, wordList);

        in.close();
        out.close();
    }

}
