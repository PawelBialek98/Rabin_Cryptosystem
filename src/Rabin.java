import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import Library.Matma;

public class Rabin {

    Matma x1[], x2[], x3[], x4[];
    Matma tab[]=new Matma[2];
    int ile_byte_z_zerami;

    ///losowanie klucza prywatnego P,Q=3(mod 4)
    public Matma getP(){
        /*Random rand = new Random();
        int p=rand.nextInt(700)*4+3;
        return p;*/
        Matma FOUR;
        FOUR = Matma.valueOf(4);
        Matma THREE;
        THREE = Matma.valueOf(3);
        Matma P;
        Random rnd = new Random();
        do {
            P = Matma.probablePrime(1024, rnd);
            //P.isProbablePrime(100);
            //System.out.println("Heja");
        } while (!P.mod(FOUR).equals(THREE));
        return P;

    }

    ///wyznaczanie klucza publicznego N=P*Q
    public Matma getN(Matma p, Matma q){
        Matma n;
        n=p.multiply(q);
        return n;
    }
    ///szyfrowanie --> C=P^2(mod N)
    public Matma[] cipher(byte[] plain, Matma n) {
        //Matma[] ciphered = new Matma[3*plain.length];
        byte[] ala = ("Ala ma kota").getBytes();
        //int tmp=plain.length;
        int block = 256 - ala.length * 8;
        byte[] plain2;
        int ile_blokow;
        if (plain.length % (block / 8) != 0) {
            ile_blokow = plain.length / (block / 8) + 1;
            ile_byte_z_zerami = 21 - plain.length % (block / 8);
            //System.out.println(ile_byte_z_zerami);
            //System.out.println("plain length: " + plain.length);
            byte[] pom = new byte[21 - plain.length % (block / 8)];
            for (int i = 0; i < ile_byte_z_zerami; i++) {
                pom[i] = 0;
            }
            plain2 = new byte[plain.length + ile_byte_z_zerami];
            //System.out.println();
            System.arraycopy(plain, 0, plain2, 0, plain.length);
            System.arraycopy(pom, 0, plain2, plain.length, pom.length);
        } else {
            plain2 = new byte[plain.length];
            ile_blokow = plain.length;
            System.arraycopy(plain, 0, plain2, 0, plain.length);
        }
        byte[] pom = new byte[32];

        Matma arr[] = new Matma[ile_blokow];

        for (int j = 0; j < ile_blokow; j++) {
            //System.out.println("Plain2 length: " + plain2.length);
            System.arraycopy(ala, 0, pom, 0, ala.length);
            System.arraycopy(plain2, j*21, pom, ala.length, 21);
            for(int i=0; i<pom.length; i++){
                System.out.println(pom[i]);
            }
            Matma pom2 = new Matma(pom);
            arr[j] = pom2;
            arr[j] = arr[j].multiply(arr[j]).mod(n);

            System.out.println(arr[j]);
        }

        return arr;
    }

        /*Matma temp;
        int pom;
        JacobiSymbol jacobiSymbol = new JacobiSymbol();
        for(int i=0 ;i<3*plain.length ;i+=3)
        {
            //byte pom = 0x00;
            //short plainShort = (short)(((plain[i] & 0xFF) << 8) | (pom & 0xFF));
            int tmp;
            pom =plain[i/3] + 128;
            tmp=(pom*pom);
            //System.out.println(pom);
            temp=Matma.valueOf(tmp);
            // System.out.println("Tekst jawny o indeksie " + i + "to: " + plain[i]);
            // System.out.print("zkwadratowany element tekstu jawnego : ");
            //System.out.println(temp.mod(n));
            //System.out.println("N: " + n);
            ciphered[i]=temp.mod(n);
            ciphered[i+1]=Matma.valueOf(pom%2);
            tmp = jacobiSymbol.computeJacobiSymbol(Matma.valueOf(pom), n);
            ciphered[i+2]=Matma.valueOf((1+tmp)/2);
            //for(int j=i; j<i+3;j++){
            //    System.out.println(ciphered[j]);
            //}
            //System.out.println("zaszyfrowany element: " + ciphered[i]);
        }*/
        //return ciphered;
    ///Algorytm euklidesa odnajdywania NWD
    ///wykorzystywany przy odszyfrowywaniu


    public Matma[] gcd(Matma a, Matma b) {

        if(!b.equals(Matma.ZERO)){
            gcd(b, a.mod(b));
            Matma pom = tab[0];
            tab[0] = tab[1].subtract((a.divide(b)).multiply(tab[0]));
            tab[1] = pom;
        }
        return tab;
    }

    public void decipher(Matma[] ciphered, Matma n, Matma p, Matma q){

        //byte[] deciphered = new byte[ciphered.length];
        Matma mp1[]=new Matma[ciphered.length];
        Matma mp2[]=new Matma[ciphered.length];
        Matma mq1[]=new Matma[ciphered.length];
        Matma mq2[]=new Matma[ciphered.length];
        Matma pom, pom2, pom3, yp, yq;
        Matma tmp[]=new Matma[2];
        ///4 pierwiastki kwadratowe z c(mod n)
        ///tylko jeden z nich zawiera zaszyfrowaną wiadomość
        Matma x1[]=new Matma[ciphered.length];
        Matma x2[]=new Matma[ciphered.length];
        Matma x3[]=new Matma[ciphered.length];
        Matma x4[]=new Matma[ciphered.length];

        tab[0] = Matma.valueOf(0);
        tab[1] = Matma.valueOf(1);
        Matma FOUR = new Matma("4");
        //pom = p.add(Matma.ONE);
        //pom = pom.divide(FOUR);
        //pom2 = q.add(Matma.ONE);
        //pom2 = pom2.divide(FOUR);
        tmp=gcd(p,q);
        yp=tmp[1];
        yq=tmp[0];
        for(int i=0 ;i<ciphered.length ;i++)
        {


            //Matma FOUR = new Matma("4");
            pom = p.add(Matma.ONE);
            pom = pom.divide(FOUR);
            mp1[i] = ciphered[i].modPow(pom, p);

            pom2 = q.add(Matma.ONE);
            pom2 = pom2.divide(FOUR);
            mq1[i] = ciphered[i].modPow(pom2, q);
///wykorzystujemy algo euklidesa żeby wyznaczyć liczby spełniające warunek yp*p+yq*q=1



            //System.out.println("Yp "+ yp);
            //System.out.println("Yq "+ yq);

            pom = (yp.multiply(p)).multiply(mq1[i]);
            pom2 = (yq.multiply(q)).multiply(mp1[i]);
            pom3 = pom.add(pom2);
            x1[i] = pom3.mod(n);
            x2[i] = n.subtract(x1[i]);

            pom3 = pom.subtract(pom2);
            x3[i] = pom3.mod(n);
            x4[i] = n.subtract(x3[i]);
        }
        this.x1=x1;
        this.x2=x2;
        this.x3=x3;
        this.x4=x4;
    }


    public byte[] convert(Matma x[]){
        byte[] converted = new byte[x.length];
        for(int i=0; i<x.length; i++){
            converted[i] = x[i].byteValue();
        }
        return converted;
    }


    public void saveToFile(byte[] cipheredText, String filePath){
        Path path = Paths.get(filePath);
        try{
            Files.write(path, cipheredText);
        }
        catch (IOException e) {
            System.out.println("Exception Occurred:");
        }
    }

    public byte[] readFromFile(String filePath){
        File plik = new File(filePath);
        byte[] fileContent = new byte[(int) plik.length()];
        FileInputStream fin = null;
        try{
            fin = new FileInputStream(plik);
            fin.read(fileContent);
        }
        catch (Exception ae){
            System.out.println("Blad " + ae);
        }
        try {
            fin.close();
        }
        catch (Exception ea){
            System.out.println("Blad " + ea);
        }
        return fileContent;
    }

} 