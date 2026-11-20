/* This is the only file you will be editing.
 * - Copyright of Starter Code: Prof. Kevin Andrea, George Mason University.  All Rights Reserved
 * - Copyright of Student Code: You!  
 * - Restrictions on Student Code: Do not post your code on any public site (eg. Github).
 * -- Feel free to post your code on a PRIVATE Github and give interviewers access to it.
 * -- You are liable for the protection of your code from others.
 * - ASCII Art Adapted from Regular Calculator by Jeremy J. Olson
 * -- Original File: https://www.asciiart.eu/electronics/calculators
 * - Date: Jan 2025
 */

/* CS367 Project 2, Fall Semester, 2025
 * Fill in your Name, GNumber, and Section Number in the following comment fields
 * Name: Aymen Esmael
 * GNumber: G01381276
 * Section Number: CS367-001/2             (Replace the _ with your section number)
 */

/* _____________________
  |  _________________  |
  | |     MUAN    3.25| |
  | |_________________| |
  |  ___ ___ ___   ___  |
  | | 7 | 8 | 9 | | + | |
  | |___|___|___| |___| |
  | | 4 | 5 | 6 | | - | |
  | |___|___|___| |___| |
  | | 1 | 2 | 3 | | x | |
  | |___|___|___| |___| |
  | | . | 0 | = | | V | |
  | |___|___|___| |___| |
  |_____________________|
 */

#include <stdio.h>
#include <stdlib.h>
#include "common_structs.h"
#include "common_definitions.h"
#include "common_functions.h"
#include "smallfp.h"


// Feel free to add many Helper Functions, Consts, and Definitions!
const int BIAS = 7;
static int howMuchtoshift(Number_s *x, int dir);
static unsigned int rounding(unsigned short number); 
// ----------Public API Functions (write these!)-------------------



/* toSmallFP - Converts a Number Struct (whole and fraction parts) into a SmallFP Value
 *  - number is managed by MUAN, DO NOT FREE number.
 *    - You may change the contents of number, but do not free it.
 *  - Follow the project documentation for this function.
 * Return the SmallFP Value or any legal SmallFP NaN representation if number is NULL.
 */
smallfp_s toSmallFP(Number_s *number) {

  smallfp_s toreturn = 0;
  if(number == NULL)//if input is null return NAN
  return ((0x3C1 | toreturn));
  
  if( (number->is_nan || (number->is_nan && number->is_infinity))){//nan input or nan and inf case
    return (0x3C1 | toreturn);//return NAN
  }

  if(number->is_infinity){//inf case
    toreturn |= number->is_negative ? (1<<10):0; //check the sign
    toreturn |= 0xF << 6;//set exp field all to 1's
    return toreturn;
  }

  if(number->whole){ //if number has a whole part

    int shifted_by = howMuchtoshift(number,1);//get how much to shift to normalize

    unsigned short discared_bit = number->whole << (16-shifted_by);//get the bits that are gone be 
    //lost while shifting to normalize

    number->fraction >>= shifted_by;//shift frac by the same amount

    number->fraction |= discared_bit;

    int E = shifted_by;//2^exp
    
    unsigned int S = number->is_negative? 1:0;
    unsigned int exp = BIAS + E;
    unsigned short frac = rounding(number->fraction);

    if(exp >= 15){//inf case while encoding
      toreturn |= number->is_negative ? (1<<10):0;
      toreturn |= 0xF << 6;
      return toreturn;
    }

    // encode
    toreturn |= (S << 10);
    toreturn |= (exp << 6);
    toreturn |= (frac);

    return toreturn;
  }
  else if(number->fraction){ // if whole is 0, but frac is not

    int shifted_by = howMuchtoshift(number,0); //get how much to shift to normalize
    number->fraction <<= shifted_by; // shift frac by that amount

    unsigned short discarded_bit = number->fraction >> (16 - shifted_by);//get the bit lost while shifiting
    number->whole |= discarded_bit;//inster the lost bit at the end of whole


    int E = -1*shifted_by;
    unsigned int S = number->is_negative? 1:0;
    int exp = BIAS + E;
    unsigned short frac = rounding(number->fraction);

    if(exp <= 0 ){//underflow while encoding
      toreturn = 0;
      toreturn |= number->is_negative? (1 << 10):0;
      return toreturn;
    }
      

    toreturn |= (S << 10);
    toreturn |= (exp << 6);
    toreturn |= (frac);

    return toreturn;

  }
  else{//if both whole and frac is zero
    return toreturn | (number->is_negative << 10);
  }

  return toreturn;  
}

/**
 * helper method to get the shifting amount to normalize
 */
static int howMuchtoshift(Number_s *x,int dir){
  int count = 0;
  if(dir){//shift whole to the left until it becomes 1.xxx
    unsigned short temp = x->whole;
    while(temp != 0){
      temp >>= 1;
      count ++;
      if(!temp>>1)
        break;
    }
    return count==0?0:count-1;
  }
  else{//shifiting frac until it is normalize
    unsigned short temp = x->fraction;
    while(count < 16){
      if(temp & 0x8000){
        break;
      }
      temp <<= 1;
      count ++;
    }
    return count+1;
  }
}

/**
 * A helper method for rounding
 */
static unsigned int rounding(unsigned short num){
  unsigned short frac = num >> 10;
  unsigned short discard_item = num & 0x03FF;
  unsigned short LSB = (discard_item >> 9) & 1;
  unsigned short MSB = frac & 1;
  unsigned short there_is_one = (discard_item & 0x01FF) != 0;

  //check if the LSB is zero
  if(LSB == 0){
    return frac;
  }

  //check if there is one after the 6th postion of the frac
  if(there_is_one){
    return frac + 1;
  }
  return (MSB)? (frac+1) : frac;
}


/* toNumber - Converts a SmallFP Value into a Number Struct (whole and fraction parts)
 *  - number is managed by MUAN, DO NOT FREE or re-Allocate number.
 *    - It is already allocated.  Do not call malloc/calloc for number.
 *  - Follow the project documentation for this function.
 *  If the conversion is successful, return 0. 
 *  - If number is NULL, return -1.
 */
int toNumber(Number_s *number, smallfp_s value) {
  //input null return -1
  if(number == NULL)
    return -1;

  int is_negative = (value & 0x400) >> 10;  
  int Exp = (value >> 6) & 0xF;
  
  unsigned short frac = (value & 0x3F);
  
  if(Exp >= 15){//if exp is inf
    number->is_negative = is_negative;
    number->is_infinity = (frac == 0);
    number->is_nan = (frac != 0);
    
      return 0;
    }

    int E = Exp - BIAS;
    if (Exp != 0) {
      frac |= 0x40; // Add implicit leading 1 for normalized numbers
  }
    frac <<= 10;

  
  unsigned short whole = (Exp > 0)? 1:0;
  unsigned short discarded_bit = 0; 

  if(E > 0){
    discarded_bit = frac >> (16 - E);
    frac <<= E;
    whole <<= E;
    whole |= discarded_bit;
  }
  else if(E < 0){
    discarded_bit = whole << (16 + E);
    frac >>= (-E);
    whole >>= (-E);
    frac |= discarded_bit;

  }

  number->is_negative = is_negative;
  number->fraction = frac;
  number->whole = whole;

  return 0;
}

/* mulSmallFP - Performs an operation on two SmallFP values
 *  - Follow the project documentation for this function.
 * Return the resulting smallfp_s value
 */
smallfp_s mulSmallFP(smallfp_s val1, smallfp_s val2) {
  //extract S and exp
  int S1 = (val1 >> 10) & 1;
  int exp1 = (val1 >> 6) & 0xF;

  int S2 = (val2 >> 10) & 1;
  int exp2 = (val2 >> 6) & 0xF;

  //special airthmetic rules:
  // input NaN check
  if (val1 == 0x3C1 || val2 == 0x3C1) {
    return 0x3C1;  // return NaN
  }

  // Multiplication with infinity
  if ((exp1 == 0xF || exp2 == 0xF)) {
    if (exp1 == 0 || exp2 == 0) {  
      return 0x3C1; // 0 * ∞ = NaN
    }
    return ((S1 ^ S2) << 10) | (0xF << 6);  // + or - ∞ * anything = + or -∞
  }

  // Multiplication with zero
  if (exp1 == 0 || exp2 == 0) {
    return ((S1 ^ S2) << 10);  // 0 * anything = 0, with appropriate sign 
  }

  // Convert to Number_s representation
  Number_s input1, input2;
  toNumber(&input1, val1);
  toNumber(&input2, val2);

  // Perform multiplication using fixed-point arithmetic
  unsigned int fixednum1 = (input1.whole << 16) | input1.fraction;//combine the whole and frac part
  unsigned int fixednum2 = (input2.whole << 16) | input2.fraction;
  unsigned long long product = (unsigned long long)fixednum1 * (unsigned long long)fixednum2;//multiply them
  
  // Extract whole and fractional parts
  unsigned short result_whole = (product >> 32) & 0xFFFF;  //shift to get the 16 bit for the whole
  unsigned short result_frac = (product >> 16) & 0xFFFF;   //shift to get the 16 bit for the frac

  // Check for overflow
  if (result_whole > 254) {
    return ((S1 ^ S2) << 10) | (0xF << 6);  // Return infinity with proper sign 
  }

  // Create Number_s representation of the result
  Number_s result_num;
  result_num.is_negative = S1 ^ S2;//gets the sign
  result_num.is_infinity = 0;
  result_num.is_nan = 0;
  result_num.whole = result_whole;
  result_num.fraction = result_frac;

  // Convert back to smallfp_s from number struct aka encode it 
  smallfp_s result = toSmallFP(&result_num);
  return result;
}
/* addSmallFP - Performs an operation on two SmallFP values
 *  - Follow the project documentation for this function.
 * Return the resulting smallfp_s value
 */
 smallfp_s addSmallFP(smallfp_s val1, smallfp_s val2) {
  //extract S and exp
  int S1 = (val1 >> 10) & 1;
  int exp1 = (val1 >> 6) & 0xF;

  int S2 = (val2 >> 10) & 0x1;
  int exp2 = (val2 >> 6) & 0xF;
  
  //check for special arithmetic rules
  if (exp1 == 0xF && exp2 == 0xF) { // ∞ + ∞, ∞ - ∞, -∞ - ∞
    if (S1 == S2) {
      return (S1 == 0) ? 0x3C0 : 0x7C0;  // Same sign -> positive or negative inf
    }
    else {
      return 0x3C1;  // Inf - Inf = NaN
    }
  }
   
  //if input is NAN
  if (val1 == 0x3C1 || val2 == 0x3C1 || val1 == 0x7C1 || val2 == 0x7C1) { //𝑁𝑎𝑁 ±𝐴𝑛𝑦𝑡ℎ𝑖𝑛𝑔=𝑁𝑎𝑁
    return 0x3C1; //return NAN
  }
   
  //if both have same values
  if ((val1 == val2) && (S1 != S2)) { // x - x, -x + x
    return 0;  // Return zero
  }
   
  //if one operand is inf
  if (exp1 == 0xF || exp2 == 0xF) {
    if (exp1 == 0xF) {
      return (S1 == 0) ? 0x3C0 : 0x7C0;  // Return +,-inf based on S1
    }
    else {
      return (S2 == 0) ? 0x3C0 : 0x7C0;  // Return +,-inf based on S2
    }
  }
 
  //both operands are zero
  if (exp1 == 0 && exp2 == 0) {
    if (S1 == 1 && S2 == 1) { 
        return (1 << 10);  // -0 - 0 = -0
    }
    return 0;  // Otherwise, return +0
  }

  //change it to number struct
  Number_s input1, input2;
  toNumber(&input1, val1);
  toNumber(&input2, val2);

  unsigned int fixednum1 = (input1.whole << 16) | input1.fraction; //combine both whole and frac part
  unsigned int fixednum2 = (input2.whole << 16) | input2.fraction;

  //Determining the sign of the result
  unsigned short S = 0;
  if (input1.is_negative == input2.is_negative) {
    S = input1.is_negative;
  }
  else {
    if(fixednum1 > fixednum2){
      S = input1.is_negative;
    }
    else if (fixednum1 < fixednum2){
      S = input2.is_negative;
    }
    else
      return 0;
  }

  // Perform addition and/or subtraction
  unsigned int calc = 0;
  if (input1.is_negative == input2.is_negative) {
    calc = fixednum1 + fixednum2;
  }
  else 
    calc = (fixednum1 > fixednum2) ? (fixednum1 - fixednum2) : (fixednum2 - fixednum1);
  

  // Extract the whole and fractional parts from the result
  unsigned short result_whole = (calc >> 16) & 0xFFFF;
  unsigned short result_frac = calc & 0xFFFF;

  if (result_whole > 254) {//overflow
    return (S << 10) | 0xF << 6; // Infinity with appropriate sign
  }

  Number_s result_num;//reconstruct the number struct to send it to smallfp to encode
  result_num.is_negative = S; 
  result_num.is_infinity = 0;
  result_num.is_nan = 0;
  result_num.whole = result_whole;
  result_num.fraction = result_frac;

  // Normalize and encode the result
  smallfp_s result = toSmallFP(&result_num);

  return result;
}

/* opSmallFP - Performs an operation on two SmallFP values
 *  - Follow the project documentation for this function.
 * Return the resulting smallfp_s value
 */ 
smallfp_s subSmallFP(smallfp_s val1, smallfp_s val2) {
  //call add method to perform subtraction since
  //subtraction is a + -b = a-b

  if (val1 == 0x3C1 || val2 == 0x3C1 || val1 == 0x7C1 || val2 == 0x7C1) { // NaN ± anything = NaN
    return 0x3C1; //return NAN
  }
  smallfp_s flip = val2 ^ 0x400;
  return addSmallFP(val1,flip);
}


/* negSmallFP - Negates a SmallFP Value.
 *  - Follow the project documentation for this function.
 * Return the resulting SmallFP Value
 */
smallfp_s negSmallFP(smallfp_s value) {
  return value ^ (1 << 10);//xor the sign bit to flip the sign
}
