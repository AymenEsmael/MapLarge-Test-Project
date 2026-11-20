public class Assignment2{


   public static int findDup (int[] a, int[] b){
   // Precondition: a not null;
   //               b not null
   //               There is some index i such that a[i] == b[i]
   //           
   // Postcondition: return the first index i at which a[i] == b[i]
   // E.g. findDup ([7, 13, 5], [5, 13, 5]) = 1
      for (int i=0; ; i++)
         if (a[i] == b[i])
            return i;
         
   }
   

   public static int findDup2 (int[] a, int[] b){
   // Precondition: True
   //           
   // Postcondition: if a or b is null throws IllegalArgumentException,
   //                return the first index i at which a[i] == b[i] if it exist
   //                else return -1
   // E.g. findDup ([7, 13, 5], [5, 1, 6]) = -1
   
      if (a == null || b == null)
         throw new IllegalArgumentException("The inputs can't be null");
      
      int n = Math.min(a.length, b.length);

      for (int i=0; i < n; i++)
         if (a[i] == b[i])
            return i;
         
      return -1;

   }
   
}