package asteroids;

public class Asteroids {

    public static void main(String[] args) {
        double counter = 0;
        for (int i = 0; i < 10000; i++) {
            if (Utils.conditionWithProbability(8)) counter++;
        }
        System.out.println(counter / 10000);
    }
    
}
