import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class Encryptor {
    public static void main(String[] args) throws IOException {
        RSADecrypt("test", RSAEncrypt("test", "help"));

        String caesarTest = caesarCipher("Death is upon us!!", 10).toString();
        System.out.println(caesarTest);
        System.out.println(caesarDeCipher(caesarTest, 10));

        String vigenereTest = vigenereCipher("Death is upon us!!", "TEXT");
        System.out.println(vigenereTest);
        System.out.println(vigenereDeCipher(vigenereTest, "TEXT"));
    }

    public static StringBuffer caesarCipher(String text, int s) {
        StringBuffer result = new StringBuffer();
        s = s % 26;
        text = text.toUpperCase();

        for (int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                char ch = (char) (((int) text.charAt(i) +
                        s - 65) % 26 + 65);
                result.append(ch);
            } else if (Character.isLowerCase(text.charAt(i))) {
                char ch = (char) (((int) text.charAt(i) +
                        s - 97) % 26 + 97);
                result.append(ch);
            }
        }
        return result;
    }

    public static StringBuffer caesarDeCipher(String text, int s) {
        StringBuffer result = new StringBuffer();
        s = (26 - s);
        text = text.toUpperCase();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                char ch = (char) (((int) text.charAt(i) +
                        s - 65) % 26 + 65);
                result.append(ch);
            } /*else if (Character.isLowerCase(text.charAt(i))) {
                char ch = (char) (((int) text.charAt(i) +
                        s - 97) % 26 + 97);
                result.append(ch);
            }*/
        }
        return result;
    }

    public static String vigenereCipher(String text, String key) {
        String res = "";
        key = key.toUpperCase();
        text = text.toUpperCase();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 'A' || c > 'Z') continue;
            res += (char) ((c + key.charAt(j) - 2 * 'A') % 26 + 'A');
            j = ++j % key.length();
        }
        return res;
    }

    public static String vigenereDeCipher(String text, String key) {
        String res = "";
        key = key.toUpperCase();
        text = text.toUpperCase();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 'A' || c > 'Z') continue;
            res += (char)((c - key.charAt(j) + 26) % 26 + 'A');
            j = ++j % key.length();
        }
        return res;
    }

    public static String RSAEncrypt(String file, String message) throws IOException {
        Scanner scan = new Scanner(new File(file));
        StringBuilder sb = new StringBuilder();
        for (char ch : message.toCharArray()) {
            sb.append((byte) ch);
        }
        BigInteger msg = new BigInteger(sb.toString());
        BigInteger n = new BigInteger(scan.nextLine());
        BigInteger e = new BigInteger(scan.nextLine());
        return (msg.modPow(e, n)).toString();
    }

    public static void RSADecrypt(String file, String message) throws IOException {
        Scanner scan = new Scanner(new File(file));
        BigInteger n = new BigInteger(scan.nextLine());
        BigInteger e = new BigInteger(scan.nextLine());
        BigInteger phi = new BigInteger(scan.nextLine());
        BigInteger d = e.modInverse(phi);
        BigInteger msg = new BigInteger(message);
        BigInteger deCrypt = msg.modPow(d, n);
        BufferedWriter out = new BufferedWriter(new FileWriter("testEnd"));
        String data = deCrypt.toString();
        StringBuilder sb = new StringBuilder();
        while (!data.equals("")) {
            sb.append(Character.toString((char) Integer.parseInt(data.substring(0, 3))));
            data = data.substring(3);
        }
        out.write(sb.toString());
        out.close();
    }

    public static void KeyGen(String name) throws IOException {
        BigInteger pr1 = primeGen();
        BigInteger pr2 = primeGen();
        BigInteger n = pr1.multiply(pr2);
        BigInteger totient = pr1.subtract(BigInteger.ONE).multiply(pr2.subtract(BigInteger.ONE));
        BigInteger e = primeGen();
        while (e.compareTo(new BigInteger("3")) < 0 && e.compareTo(totient) > 0 && !e.gcd(totient).equals(BigInteger.ONE))
            e = primeGen();
        BufferedWriter out = new BufferedWriter(new FileWriter(name));
        out.write(n.toString());
        out.newLine();
        out.write(e.toString());
        out.newLine();
        out.write(totient.toString());
        out.close();
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
        Random rand = new Random();
        BigInteger a = new BigInteger("2").add(new BigInteger(256, rand).mod(primePoss.subtract(new BigInteger("4"))));
        BigInteger x = a.modPow(d, primePoss);
        if (x.equals(new BigInteger("1")) || x.equals(primePoss.subtract(BigInteger.ONE)))
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