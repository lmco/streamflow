/**
 * Copyright 2014 Lockheed Martin Corporation
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
package streamflow.server.resource;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import streamflow.model.FileInfo;
import streamflow.service.FileService;
import streamflow.service.util.IDUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;

@Path("/files")
public class FileResource {

    private final FileService fileService;

    private static final Detector detector = new DefaultDetector(
            MimeTypes.getDefaultMimeTypes());

    @Inject
    public FileResource(FileService fileService) {
        this.fileService = fileService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<FileInfo> listFiles() {
        return fileService.listFiles();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public FileInfo createFile(@FormDataParam("file") byte[] fileContent,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @HeaderParam("Content-type") String contentType) {
        // Validate the input parameters
        if (fileContent == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("File content was specified in the request").build());
        }

        // Save the upload metadata for later retrieval
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(IDUtils.randomUUID());
        fileInfo.setFileName(fileDetail.getFileName());
        fileInfo.setFileSize(fileContent.length);
        fileInfo.setCreated(new Date());
        fileInfo.setModified(fileInfo.getCreated());
        fileInfo.setContentHash(DigestUtils.md5Hex(fileContent));

        try {
            // Reliably detect the mime type using Tika
            fileInfo.setFileType(detector.detect(
                    new ByteArrayInputStream(fileContent), new Metadata()).toString());
        } catch (IOException ex) {
            // Use a default type of binary just in case
            fileInfo.setFileType("application/octet-stream");
        }

        // Persist the upload metadata after the file is successfully written
        fileInfo = fileService.saveFile(fileInfo, fileContent);

        return fileInfo;
    }

    @GET
    @Path("/{fileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public FileInfo getFile(@PathParam("fileId") String fileId) {
        return fileService.getFileInfo(fileId);
    }

    @PUT
    @Path("/{fileId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateFile(@PathParam("fileId") String fileId, FileInfo fileInfo) {
        fileService.updateFile(fileId, fileInfo);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{fileId}")
    public Response deleteFile(@PathParam("fileId") String fileId) {
        fileService.deleteFile(fileId);
        return Response.ok().build();
    }

    @GET
    @Path("/{fileId}/content")
    public Response getFileContent(@PathParam("fileId") String fileId) {
        byte[] fileContent = fileService.getFileContent(fileId);

        // Retrieve the file metadata to build the request
        FileInfo fileInfo = fileService.getFileInfo(fileId);

        return Response.ok().entity(fileContent).type(fileInfo.getFileType())
                .header("Content-Disposition", "attachment; filename=" + fileInfo.getFileName())
                .header("Cache-Control", "no-cache").build();
    }

    @POST
    @Path("/{fileId}/content")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public FileInfo updateFileContent(@PathParam("fileId") String fileId,
            @FormDataParam("file") byte[] fileContent,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @HeaderParam("Content-type") String contentType) {

        // Validate the input parameters
        if (fileContent == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("File data was not provided in the request").build());
        }

        FileInfo fileInfo = fileService.getFileInfo(fileId);
        fileInfo.setFileName(fileDetail.getFileName());
        fileInfo.setFileSize((long) fileContent.length);
        fileInfo.setModified(new Date());
        fileInfo.setContentHash(DigestUtils.md5Hex(fileContent));

        try {
            // Reliably detect the mime type using Tika
            fileInfo.setFileType(detector.detect(
                    new ByteArrayInputStream(fileContent), new Metadata()).toString());
        } catch (IOException ex) {
            // Use a default type of binary just in case
            fileInfo.setFileType("application/octet-stream");
        }

        // Persist the upload metadata after the file is successfully written
        fileService.updateFile(fileId, fileInfo, fileContent);

        return fileInfo;
    }
}
