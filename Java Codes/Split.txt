class Main {
    public static void main(String[] args) {
        String myString = "The quick brown fox jump over a lazy dog";
        String toGet[] = myString.split(" ");
        for(String x:toGet){
            System.out.println(x);
        }
    }
}