public class CalMutants
{
    private int faultId;

    public CalMutants(int faultID){
        this.faultId = faultID;
    }
    int cal (int month1, int day1, int month2, int day2, int year)
   {
      int numDays;
      if (month2 == month1) // in the same month
        if(faultId == 1) numDays = day2 % day1; //Δ1 
        else if(faultId ==2) numDays = day2; //Δ2 
        else  numDays = day2 - day1;
    else {
         // The first array element is a placeholder because there is no month zero
        int[] daysIn;
        if (faultId ==3) daysIn = new int[] {0, 31, 0, 31, 30, 31, 30, 31, 30, 30, 31, 30, 31}; //Δ3 
        else if(faultId == 4)  daysIn =new int[] {0, 31, 0, 31, 31, 30, 30, 31, 30, 30, 31, 30, 31}; //Δ4 
        else if(faultId ==5)  daysIn = new int[]{0, 31, 0, 31, 30, 31, 30, 31, 30, 30, 31, 31, 31}; //Δ5
        else  daysIn = new int[] {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

         // Are we in a leap year?
         int m4;
        m4 = year % 4;
        if(faultId == 6) m4 = year / 4; //Δ6 
        int m100;
        m100 = year % 100;
        if(faultId == 7) m100 = year / 100; //Δ7
        if(faultId==8) m100 = year % 400; //Δ8
        int m400;
         m400 = year % 400;
        if (faultId == 9) m400 = year + 400; //Δ9
        if(faultId == 10) m400 = year % 100; //Δ10

        boolean cond;
        if (faultId == 11) cond = ((m4 != 0) || ((m100 == 0) || (m400 != 0))); //Δ11
        else if(faultId ==12) cond = ((m4 != 0) || ((m100 != 0) && (m400 != 0))); //Δ12
        else if(faultId ==13) cond = ((m4 == 0) || ((m100 == 0) && (m400 != 0))); //Δ13
        else if(faultId==14) cond = ((m4 != 0) && ((m100 == 0) && (m400 != 0))); //Δ14
        else cond = ((m4 != 0) || ((m100 == 0) && (m400 != 0)));

        if (cond)
            
            daysIn[2] = (faultId ==15)? 29:28; //Δ15
         else
            daysIn[2] = 29;
            if (faultId == 16) daysIn[3] = 29; //Δ16
         // start with days in the two months
         
         if(faultId == 17) numDays = day2 + (daysIn[month1+1] - day1); //Δ17
        else if (faultId == 18) numDays = day2 + (daysIn[month1]); //Δ18
         else numDays = day2 + (daysIn[month1] - day1);
         // add the days in the intervening months
        if (faultId ==19)
            for (int i = month1 + 1; i <= month2; i++) //Δ19
                numDays = daysIn[i] + numDays;
        else if (faultId == 20)
            for (int i = month1; i < month2; i++) //20
                numDays = daysIn[i] + numDays;
        else
            for (int i = month1 + 1; i < month2; i++)
                numDays = daysIn[i] + numDays;
      }
      return (numDays);
   }
}