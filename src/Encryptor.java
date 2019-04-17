import org.apache.commons.cli.*;


import java.math.BigInteger;
import java.security.PublicKey;
import java.security.Security;
import java.util.Random;

public class Encryptor {
    public static void main(String[] args) {
        String PRIVATE, PUBLIC;
        BigInteger pr1 = primeGen();
        BigInteger pr2 = primeGen();
        BigInteger n = pr1.multiply(pr2);
        BigInteger totient = pr1.subtract(BigInteger.ONE).multiply(pr2.subtract(BigInteger.ONE));
        BigInteger e = primeGen();
        while(e.compareTo(new BigInteger("3"))<0&&e.compareTo(totient)>0&&!e.gcd(totient).equals(BigInteger.ONE))
            e=primeGen();
        PUBLIC = n.multiply(e).toString();


    }

    private static BigInteger primeGen() {
        BigInteger num = BigInteger.probablePrime(256, new Random());
        if (checkPrime(num)) {
            return num;
        }
        return primeGen();
    }

    private static boolean checkPrime(BigInteger primePossibly) {
        if (primePossibly.compareTo(BigInteger.ONE) < 0 || primePossibly.equals(new BigInteger("4")))
            return false;
        if (primePossibly.compareTo(new BigInteger("3")) < 0)
            return true;

        BigInteger d = primePossibly.subtract(BigInteger.ONE);

        while (d.mod(new BigInteger("2")).equals(new BigInteger("2")))
            d = d.divide(new BigInteger("2"));

        for (int x = 0; x < 200; x++) {
            if (!miller(d, primePossibly))
                return false;
        }

        return true;
    }

    private static boolean miller(BigInteger d, BigInteger primePoss) {
        Random rand=  new Random();
        BigInteger a =  new BigInteger("2").add(new BigInteger(256,rand).mod(primePoss.subtract(new BigInteger("4"))));
        // Compute a^d % n
        BigInteger x = a.modPow(d,primePoss);

        if (x.equals(new BigInteger("1"))|| x.equals(primePoss.subtract(BigInteger.ONE)))
            return true;

        // Keep squaring x while one of the
        // following doesn't happen
        // (i) d does not reach n-1
        // (ii) (x^2) % n is not 1
        // (iii) (x^2) % n is not n-1
        while (!d.equals(primePoss.subtract(BigInteger.ONE))) {
            x = (x.multiply(x)).mod(primePoss);
            d = d.multiply(new BigInteger("2"));

            if (x.equals(BigInteger.ONE))
                return false;
            if (x.equals(primePoss.subtract(BigInteger.ONE)))
                return true;
        }

        // Return composite
        return false;
    }
}