import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.ParameterizedTest;

import java.beans.Transient;

public class TestCal{
    @ParameterizedTest
    @CsvSource({
        "1,20,1,20,2025, 0",  // Same Month same Date
        "1,10,1,30,2025,20", // Same Month Diff Date
        "1,1,2,1,2025,31",   // Diff Month same Date 
        "4,15,5,25,2025,40",      //Diff Month Diff Date
        
        //Detailed test for each month
        "1,1,2,28,2025,58",//jan to fab(non leap year)
        "1,1,3,31,2025,89",
        "1,1,4,30,2025,119",
        "1,1,5,31,2025,150",
        "1,1,6,30,2025,180",
        "1,1,7,31,2025,211",
        "1,1,8,31,2025,242",
        "1,1,9,30,2025,272",
        "1,1,10,31,2025,303",
        "1,1,11,30,2025,333",
        "1,1,12,31,2025,364",
        
        //Full Leap year test
        "1,1,12,31,2024,365"
    })
    public void TestCalS(int m1, int d1, int m2, int d2, int year, int exp){
        int res = Cal.cal(m1,d1,m2,d2,year);
        assertEquals(exp,res);
    }

    @ParameterizedTest
    @CsvSource({
        // month1, day1, month2, day2, year, expectedDays
        "2,1,3,1,2024,29",  // divisible by 4
        "2,1,3,1,1900,28",  // divisible by 100, not 400
        "2,1,3,1,2000,29",  // divisible by 400
        "2,1,3,1,2025,28"   // non-leap year
    })
    public void testLeapYearVariants(int m1, int d1, int m2, int d2,
                              int year, int expected) {
        assertEquals(expected, Cal.cal(m1, d1, m2, d2, year));
    }

}
