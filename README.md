# Crypto Loacker

Small Java Swing application to encrypt/decrypt files in a local directory.  
The interface allows selecting a directory, choosing the algorithm, entering a key and IV (if required) and starting the ENCRYPT/DECRYPT operation.

## Main features
- Graphical interface in `src/UI.java`.
- Encryption/decryption logic in `src/Loacker.java`.
- Supported algorithms: `DES-ECB`, `DES-CBC`, `AES-256-ECB`, `AES-256-CBC`.
- Encrypted files are saved with the `.loacker` extension and original files are deleted.
- Non-recursive operations: only files in the selected directory are processed (no subdirectories).
- Shorter keys/IVs are zero-padded; keys/IVs that are too long or empty are rejected.

## Requirements
- Java 11+ recommended (it also works with JDK 8; pay attention to JCE policy for AES-256).
- IntelliJ IDEA (the development environment used; the project can also be run from the command line on Windows).

> Security note: always make a backup of your data. If you use the wrong key/IV the files will not be recoverable.

## How to run

### From IntelliJ IDEA
1. Open the project in IntelliJ IDEA.
2. Open `src/UI.java`.
3. Run the class with the `main` method.

### From the command line
1. Open Command Prompt in the src directory.
2. Compile:
```cmd
c -d out src\*.java
```
3. Run:
```cmd
java -cp out UI
```
## Usage
1. Click `Scegli directory` and select the folder containing the files to process.
2. Select the algorithm (`DES-ECB`, `DES-CBC`, `AES-256-ECB`, `AES-256-CBC`).
3. Enter the key in the `Enter the key:` field (must not be empty or longer than the required format).
4. If a `CBC` mode is chosen, also enter the IV in the `Enter the IV:` field (required for CBC).
5. Press `ENCRYPT` to encrypt or `DECRYPT` to decrypt.

## Key / IV rules
1. DES: maximum key `8` bytes.
2. AES-256: maximum key `32` bytes.
3. IV for CBC: maximum `16` bytes.
4. Shorter keys/IVs are zero-padded; inputs that are too long are rejected.

## Limitations and warnings
1. Destructive operations: original files are deleted after encryption. Make backups.
2. Does not process subdirectories.
3. If the key/IV do not match the original encryption, data will be corrupted and irrecoverable.
4. For AES-256 on older JDKs you may need to enable the `Unlimited Strength Jurisdiction Policy`.

## Main file structure
1. `src/UI.java` — Swing GUI and event handling.
2. `src/Loacker.java` — encryption/decryption logic, validation and normalization of key/IV.