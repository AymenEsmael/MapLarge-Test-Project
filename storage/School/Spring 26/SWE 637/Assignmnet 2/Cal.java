public class Cal
{
   public static int cal (int month1, int day1, int month2,
                          int day2, int year)
   {
   //***********************************************************
   // Calculate the number of days between the two given days in
   // the same year in accordance with the Gregorian calendar.
   // Examples: January 1 to January 1 is 0 days
   //           January 1 to January 2 is 1 day
   //           January 1 to January 31 is 30 days
   // Preconditions:
   //   1. day1 and day2 must be in same year
   //   2. 1 <= month1, month2 <= 12
   //   3. 1 <= day1 <= maxDaysInMonth1
   //      where maxDaysInMonth1 is defined as the largest valid
   //      day in the specified month1 and year, including any
   //      leap year, as per the Gregorian calendar
   //   4. 1 <= day2 <= maxDaysInMonth2
   //      where maxDaysInMonth2 is defined as the largest valid
   //      day in the specified month2 and year, including any
   //      leap year, as per the Gregorian calendar
   //   5. month1 <= month2
   //   6. month1 == month2 implies day1 <= day2
   //   7. 1 <= year <= 10000
   //***********************************************************
      int numDays;
 
      if (month2 == month1) // in the same month
         numDays = day2 - day1;// Fault should be day2-day1
      else {
         // The first array element is just a placeholder
         // because there is no month zero
         int daysIn[] = {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};// fault index 8 => 31
         // Are we in a leap year?
         int m4 = year % 4;
         int m100 = year % 100;
         int m400 = year % 400;// fault year % 400
         if ( ((m4 == 0) && (m100 != 0)) || (m400 == 0))// fault
            daysIn[2] = 29;
         else
            daysIn[2] = 28;
 
         // start with days in the two months
         numDays = day2 + (daysIn[month1] - day1);
 
         // add the days in the intervening months
         for (int i = month1 + 1; i < month2; i++)// fault on the loop condiditon 
            numDays = daysIn[i] + numDays;
      }
      return (numDays);
   }
}

// public class Cal
// {
//    public static int cal (int month1, int day1, int month2,
//                           int day2, int year)
//    {
//    //***********************************************************
//    // Calculate the number of days between the two given days in
//    // the same year in accordance with the Gregorian calendar.
//    // Examples: January 1 to January 1 is 0 days
//    //           January 1 to January 2 is 1 day
//    //           January 1 to January 31 is 30 days
//    // Preconditions:
//    //   1. day1 and day2 must be in same year
//    //   2. 1 <= month1, month2 <= 12
//    //   3. 1 <= day1 <= maxDaysInMonth1
//    //      where maxDaysInMonth1 is defined as the largest valid
//    //      day in the specified month1 and year, including any
//    //      leap year, as per the Gregorian calendar
//    //   4. 1 <= day2 <= maxDaysInMonth2
//    //      where maxDaysInMonth2 is defined as the largest valid
//    //      day in the specified month2 and year, including any
//    //      leap year, as per the Gregorian calendar
//    //   5. month1 <= month2
//    //   6. month1 == month2 implies day1 <= day2
//    //   7. 1 <= year <= 10000
//    //***********************************************************
//         int numDays;
 
//         if (month2 == month1) // in the same month
//         //  numDays = day2 % day1;//fault [should be day2-day1]
//         numDays = day2 - day1;
//         else {
//             // The first array element is just a placeholder
//             // because there is no month zero
//             int daysIn[] = {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};//fault index 8 => 31
//             // Are we in a leap year?
//             int m4 = year % 4;
//             int m100 = year % 100;
//             int m400 = year % 400;//fault it should be year % 400
//             if (((m4 == 0) && (m100 != 0)) || (m400 == 0))//fault (m4==0 and m100 != 0) or (m400 ==0)
//                 daysIn[2] = 29;
//             else
//                 daysIn[2] = 28;
    
//             // start with days in the two months
//             numDays = day2 + (daysIn[month1] - day1);
    
//             // add the days in the intervening months
//             for (int i = month1 + 1; i < month2; i++)// fault should be i < month2
//                 numDays = daysIn[i] + numDays;
//         }
//         // System.out.println(numDays);
//         return (numDays);
//    }
//     public static void main(String[] args){
//         int res = cal(1, 10, 1, 30, 2025);
//         // System.out.println(res);
//     }
// }
