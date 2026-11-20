
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.params.provider.CsvFileSource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalPairwiseTest {

    @ParameterizedTest
    @CsvFileSource(files = "TestPairwise.csv", numLinesToSkip = 1)
    void testPairwise(int id, int year, int month1, int day1,
                      int month2, int day2) {

        // Oracle using Java time API
        LocalDate d1 = LocalDate.of(year, month1, day1);
        LocalDate d2 = LocalDate.of(year, month2, day2);
        long expected = ChronoUnit.DAYS.between(d1, d2);

        CalMutants cm = new CalMutants(17);

        int actual = cm.cal(month1, day1, month2, day2, year);

        assertEquals(expected, actual);
    }
    
    // @ParameterizedTest
    // @CsvFileSource(files = " TestAllCombinations.csv", numLinesToSkip = 1)
    // void TestAllCombinations(int id, int year, int month1, int day1,
    //                   int month2, int day2) {

    //     // Oracle using Java time API
    //     LocalDate d1 = LocalDate.of(year, month1, day1);
    //     LocalDate d2 = LocalDate.of(year, month2, day2);
    //     long expected = ChronoUnit.DAYS.between(d1, d2);

    //     CalMutants cm = new CalMutants(20);

    //     int actual = cm.cal(month1, day1, month2, day2, year);

    //     assertEquals(expected, actual);
    // }
    
}