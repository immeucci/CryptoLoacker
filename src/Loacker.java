import java.io.File;

public class Loacker {
    private File choosenDirectory;
    private String algorithm, key, iv;

    public Loacker(File choosenDirectory, String algorithm, String key, String iv) {
        this.choosenDirectory = choosenDirectory;
        this.algorithm = algorithm;
        this.key = key;
        this.iv = iv;
    }


}
