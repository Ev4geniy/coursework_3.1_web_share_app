package my.cursework.model;

public class Checker {
    public static boolean isClean(String s){
        if (s == null){
            System.out.format("S IS NULL\n");
        }
        for(char c : s.toCharArray()){
            if(!(Character.toString(c).toLowerCase()).matches("[a-zA-Z0-9]")){
                return false;
            }
        }
        return true;
    }

    public static boolean isCleanPassword(String s){
        for(char c : s.toCharArray()){
            if(!(Character.toString(c).toLowerCase()).matches("[a-zA-Z0-9!@#$%\\^&*+]")){
                return false;
            }
        }
        return true;
    }
}
