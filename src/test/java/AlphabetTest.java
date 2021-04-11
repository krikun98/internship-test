import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlphabetTest {

    @Test
    void alphabet() {
        List<String> alphabet_test = Arrays.asList("caa", "aaa", "aab");
        assertEquals("zyxwvutsrqponmlkjihgfedcab", Alphabet.alphabet(alphabet_test));
        alphabet_test = Arrays.asList("abc", "abb", "cba", "bac");
        assertEquals("zyxwvutsrqponmlkjihgfedacb", Alphabet.alphabet(alphabet_test));
        alphabet_test = Arrays.asList("abc", "abb", "cba", "bac", "cab");
        assertEquals("Impossible", Alphabet.alphabet(alphabet_test));
    }
}