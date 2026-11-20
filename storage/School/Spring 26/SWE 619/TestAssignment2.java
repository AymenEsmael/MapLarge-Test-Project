import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.beans.Transient;

public class TestAssignment2 {

    @ParameterizedTest
    @CsvSource({
        " '1,2,3' , '1,4,5', 0", //match only at index 0
        " '1,2,3' , '4,2,5', 1", //match only at index 1
        " '1,2,3' , '4,5,3', 2", //match only at index 2
        " '1,2,3' , '4,2,3', 1", //2 match 
        " '1,2,3' , '1,2,3', 0", //3 match 
    })
    public void TestFindDup(String a, String b, int expected){
        a = a.replace("'","").trim();
        b = b.replace("'", "").trim();

        String[] alist = a.split(",");
        String[] blist = b.split(",");

        int[] inta = new int[alist.length];
        int[] intb = new int[blist.length];
        for (int i=0; i< alist.length; i++){
            inta[i] = Integer.parseInt(alist[i]);
            intb[i] = Integer.parseInt(blist[i]);
        }
        int res = Assignment2.findDup(inta, intb);
        assertEquals(expected, res);
                
    }

    @ParameterizedTest
    @CsvSource({
        " '1,2,3' , '1,4,5', 0", //match only at index 0
        " '1,2,3' , '4,2,5', 1", //match only at index 1
        " '1,2,3' , '4,5,3', 2", //match only at index 2
        " '1,2,3' , '4,2,3', 1", //2 match 
        " '1,2,3' , '1,2,3', 0", //3 match 
        " '1,2,3' , '4,5,6', -1", //No match 
        " '1,2' , '8,2,3', 1",   // Diff length, a < b
        " '1,2,3', '1,3', 0"     //Dif length, a > b
        

    })
    public void TestFindDup2(String a, String b, int expected){
        a = a.replace("'","").trim();
        b = b.replace("'", "").trim();

        String[] alist = a.split(",");
        String[] blist = b.split(",");

        int[] inta = new int[alist.length];
        int[] intb = new int[blist.length];
        for (int i=0; i< alist.length; i++){
            inta[i] = Integer.parseInt(alist[i]);
        }
        for (int i=0;i < blist.length; i++)
            intb[i] = Integer.parseInt(blist[i]);
        int res = Assignment2.findDup2(inta, intb);
        assertEquals(expected, res);

    }

    @Test
    public void testFindDup2_NullArray() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assignment2.findDup2(null, new int[]{1,2,3});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Assignment2.findDup2(new int[]{1,2,3}, null);
        });
    }
    @Test
    public void testFindDup2EmptyArray(){
        assertEquals(-1, (Assignment2.findDup2(new int[]{}, new int[] {})));
    }

    @Test 
    public void testFindDup2_OneEmpty(){
        assertEquals(-1, (Assignment2.findDup2(new int[]{7,2,3}, new int[]{})));
        assertEquals(-1, (Assignment2.findDup2( new int[]{}, new int[]{7,2,3})));

    }
}
