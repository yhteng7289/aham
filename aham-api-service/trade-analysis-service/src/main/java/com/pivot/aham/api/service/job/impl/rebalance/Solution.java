package com.pivot.aham.api.service.job.impl.rebalance;

class Solution {
    public int[] pivotIndex(int[] digits) {

        int lastIndex = digits.length-1;
        while (lastIndex>=0){
            int tmp = digits[lastIndex]+1;
            if(tmp == 10){
                digits[lastIndex] = 0;
                lastIndex--;
            }else {
                digits[lastIndex]=tmp;
                break;
            }

        }
        if(lastIndex == -1){
            int[] newDigits = new int[digits.length+1];
            newDigits[0] = 1;
            for(int i=0;i<digits.length;i++){
                newDigits[i+1]=digits[i];
            }
            return newDigits;

        }else{
            return digits;
        }



    }
    public static void main(String[] args){
        Solution solution = new Solution();
        int[] nums = {1,2,3};
        System.out.println(solution.pivotIndex(nums));
    }
}