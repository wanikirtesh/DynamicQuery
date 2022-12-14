package com.sbn.misc.dynamic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtractCreator {
    private static final String ZIP_UTIL = "C:\\Users\\idnkiw\\Desktop\\Script\\7-Zip\\7z.exe";
    public static void main(String[] args) throws IOException {

        String strOriginalExtractPath = "C:\\Users\\idnkiw\\Downloads\\VPLASHH.20221130.0201.tar.Z",prefix = "P",postfix="";
        Path originalExtractPath = Path.of(strOriginalExtractPath);
        String originalExtractName = originalExtractPath.getFileName().toString();
        int startProperty=20251,propertyCount=1;
        for(int i=startProperty;i<startProperty+propertyCount;i++){
            String newPropertyCode = prefix + i + postfix;
            String existingPropertyCode = originalExtractName.split("\\.")[0];
            String newExtractName = originalExtractName.replace(existingPropertyCode, newPropertyCode);
            System.out.println(newExtractName);
            Path newFilesDir = updateFiles(existingPropertyCode, newPropertyCode, originalExtractPath);
            zipFiles(newFilesDir,newExtractName);
        }
    }

    private static void zipFiles(Path newFilesDir, String newExtractName) throws IOException {
        Path newExt = Paths.get(String.valueOf(newFilesDir), newExtractName.replace(".z", ""));
        String[] params = {ZIP_UTIL,"a -ttar \""+newExt+"\""};
        Process p = Runtime.getRuntime().exec(params);
        try(BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static Path updateFiles(String existingPropertyCode, String newPropertyCode, Path originalExtractPath) throws IOException {
        Path newFilesLocation = Paths.get(String.valueOf(originalExtractPath.getParent()),newPropertyCode);
        if(Files.exists(newFilesLocation)){
            Files.walk(newFilesLocation).map(Path::toFile).forEach(File::delete);
        }else
        Files.createDirectory(newFilesLocation);
        Files.walk(Paths.get(String.valueOf(originalExtractPath.getParent()),existingPropertyCode)).forEach(filePath ->{
            if(!Files.isDirectory(filePath))
            updateFileContent(filePath,newFilesLocation,existingPropertyCode,newPropertyCode);
        });
        return newFilesLocation;
    }

    private static void updateFileContent(Path filePath, Path destDirectory, String existingPropertyCode, String newPropertyCode) {
        String newFileName = filePath.getFileName().toString().replace(existingPropertyCode,newPropertyCode);
        Path targetFilePath = Paths.get(String.valueOf(destDirectory),newFileName);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toFile()));BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetFilePath.toFile()))) {
            String line;
            while ((line = bufferedReader.readLine())!=null){
                bufferedWriter.write(line.replace(existingPropertyCode,newPropertyCode));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
