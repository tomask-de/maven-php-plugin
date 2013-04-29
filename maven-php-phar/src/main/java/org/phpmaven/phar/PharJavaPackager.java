/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.phar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.phar.PharEntry.EntryType;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpException;

/**
 * Phar packager implementation to use php-exe.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPharPackager.class, hint = "JAVA", instantiationStrategy = "per-lookup")
public class PharJavaPackager implements IPharPackager {
    
    private static final boolean DEBUG = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void packagePhar(IPharPackagingRequest request, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        try {
            final File pharFile = new File(request.getTargetDirectory(), request.getFilename());
            final FileOutputStream fos = new FileOutputStream(pharFile);
            fos.write("<?php ".getBytes("UTF-8"));
            fos.write(request.getStub().getBytes("UTF-8"));
            fos.write(" __HALT_COMPILER(); ?>".getBytes("UTF-8"));
            fos.write(13);
            fos.write(10);
            
            final ByteArrayOutputStream fileEntriesBaos = new ByteArrayOutputStream();
            final ByteArrayOutputStream compressedFilesBaos = new ByteArrayOutputStream();
            int fileCount = 0;
            for (final PharEntry entry : request.getEntries()) {
                if (entry.getType() == EntryType.DIRECTORY) {
                    final PharDirectory dir = (PharDirectory) entry;
                    final File dirToPack = dir.getPathToPack();
                    for (final File fileToPack : FileUtils.listFiles(dirToPack, null, true)) {
                        final String relPath = fileToPack.getAbsolutePath().substring(
                                dirToPack.getAbsolutePath().length());
                        String filePath = (dir.getRelativePath() + relPath).replace("\\", "/");
                        while (filePath.startsWith("/")) {
                            filePath = filePath.substring(1);
                        }
                        fileCount++;
                        packFile(fileEntriesBaos, compressedFilesBaos, fileToPack, filePath);
                    }
                } else {
                    final PharFile file = (PharFile) entry;
                    final File fileToPack = file.getFile();
                    String filePath = file.getLocalName().replace("\\", "/");
                    while (filePath.startsWith("/")) {
                        filePath = filePath.substring(1);
                    }
                    fileCount++;
                    packFile(fileEntriesBaos, compressedFilesBaos, fileToPack, filePath);
                }
            }
            
            // build metadata
            final ByteArrayOutputStream metadataBaos = new ByteArrayOutputStream();
            if (request.getMetadata().isEmpty()) {
                writeIntLE(metadataBaos, 0);
            } else {
                final StringBuffer metadata = new StringBuffer();
                metadata.append("a:");
                metadata.append(request.getMetadata().size());
                metadata.append(":{");
                for (Entry<String, String> entry : request.getMetadata().entrySet()) {
                    metadata.append("s:");
                    metadata.append(entry.getKey().length());
                    metadata.append(":\"");
                    metadata.append(entry.getKey());
                    metadata.append("\";s:");
                    metadata.append(entry.getValue().length());
                    metadata.append(":\"");
                    metadata.append(entry.getValue());
                    metadata.append("\";");
                }
                metadata.append("}");
                final byte[] metabytes = metadata.toString().getBytes("UTF-8");
                writeIntLE(metadataBaos, metabytes.length);
                metadataBaos.write(metabytes);
            }
            final byte[] metadata = metadataBaos.toByteArray();
            
            final byte[] fileEntries = fileEntriesBaos.toByteArray();
            
            final byte[] pharAlias = (request.getAlias() == null || request.getAlias().length() == 0) ? 
            		pharFile.getName().getBytes("UTF-8") :
            		request.getAlias().getBytes("UTF-8");
            final int manifestLength = metadata.length + fileEntries.length + pharAlias.length + 14;
            writeIntLE(fos, manifestLength);
            writeIntLE(fos, fileCount);
            
            // version
            fos.write(0x11);
            fos.write(0);
            
            // bits: 0x00010000, with signature
            fos.write(0);
            fos.write(0);
            fos.write(1);
            fos.write(0);
            
            // phar file alias
            writeIntLE(fos, pharAlias.length);
            fos.write(pharAlias);
            
            // add metadata
            fos.write(metadata);
            
            fos.write(fileEntries);
            fos.write(compressedFilesBaos.toByteArray());
            fos.flush();
            
            // signature
            final MessageDigest cript = MessageDigest.getInstance("SHA-1");
            cript.update(FileUtils.readFileToByteArray(pharFile));
            final byte[] sha1 = cript.digest();
            fos.write(sha1);
            for (int i = sha1.length; i < 20; i++) {
                fos.write(0);
            }
            // SHA1 signature
            writeIntLE(fos, 2);
            // Signature magic
            fos.write("GBMB".getBytes());
            
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            throw new PhpCoreException("Error building phar", ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new PhpCoreException("Error building phar", ex);
        }
    }

    private void packFile(
        final ByteArrayOutputStream fileEntriesBaos,
        final ByteArrayOutputStream compressedFilesBaos,
        final File fileToPack,
        String filePath)
        throws IOException {
        if (DEBUG) {
            System.out.println("Packing file " + fileToPack + " with " + fileToPack.length() + " bytes.");
        }
        
        final byte[] fileBytes = filePath.getBytes("UTF-8");
        writeIntLE(fileEntriesBaos, fileBytes.length);
        fileEntriesBaos.write(fileBytes);
        // TODO Complain with files larger than 4 bytes file length
        writeIntLE(fileEntriesBaos, (int) fileToPack.length());
        writeIntLE(fileEntriesBaos, (int) (fileToPack.lastModified() / 1000));
        
        final byte[] uncompressed = FileUtils.readFileToByteArray(fileToPack);
        if (DEBUG) {
            System.out.println("read " + uncompressed.length + " bytes from file.");
        }
        final ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
//        final GZIPOutputStream gzipStream = new GZIPOutputStream(compressedStream);
//        gzipStream.write(uncompressed);
//        gzipStream.flush();
        final CRC32 checksum = new CRC32();
        checksum.update(uncompressed);
        final Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(uncompressed);
        deflater.finish();
        final byte[] buf = new byte[Short.MAX_VALUE];
        while (!deflater.needsInput()) {
            final int bytesRead = deflater.deflate(buf);
            compressedStream.write(buf, 0, bytesRead);
        }
        
        final byte[] compressed = compressedStream.toByteArray();
        if (DEBUG) {
            System.out.println("compressed to " + compressed.length + " bytes.");
        }

//        final Inflater decompresser = new Inflater();
//        decompresser.setInput(compressed);
//        byte[] result = new byte[5000];
//        try {
//            int resultLength = decompresser.inflate(result);
//            final String str = new String(result, 0, resultLength);
//            int i = 42;
//        } catch (DataFormatException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        decompresser.end();
        
        compressedFilesBaos.write(compressed);
        writeIntLE(fileEntriesBaos, compressed.length);

        writeIntLE(fileEntriesBaos, checksum.getValue());

        // bits: 0x00001000, gzip
        fileEntriesBaos.write(0);
        fileEntriesBaos.write(0x10);
        fileEntriesBaos.write(0);
        fileEntriesBaos.write(0);
        
        // 0 bytes manifest
        writeIntLE(fileEntriesBaos, 0);
    }

    /**
     * Writes 4 bytes in little endian order.
     * @param baos
     * @param v
     * @throws IOException 
     */
    private void writeIntLE(OutputStream baos, int v) throws IOException {
        baos.write((v >>>  0) & 0xFF);
        baos.write((v >>>  8) & 0xFF);
        baos.write((v >>> 16) & 0xFF);
        baos.write((v >>> 24) & 0xFF);
    }

    /**
     * Writes 4 bytes in little endian order.
     * @param baos
     * @param v
     * @throws IOException 
     */
    private void writeIntLE(OutputStream baos, long v) throws IOException {
        baos.write((int) ((v >>>  0) & 0xFF));
        baos.write((int) ((v >>>  8) & 0xFF));
        baos.write((int) ((v >>> 16) & 0xFF));
        baos.write((int) ((v >>> 24) & 0xFF));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readStub(File pharPackage, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        // TODO add support
        throw new PhpCoreException("not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractPharTo(File pharPackage, File targetDirectory, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        // TODO add support
        throw new PhpCoreException("not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> listFiles(File pharPackage, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        // TODO add support
        throw new PhpCoreException("not supported");
    }

}
