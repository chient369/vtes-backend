package com.vtes.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vtes.entity.FileData;
import com.vtes.exception.NotFoundException;
import com.vtes.exception.UploadFileException;
import com.vtes.model.ResponseData;
import com.vtes.model.ResponseData.ResponseType;
import com.vtes.security.service.UserDetailsImpl;
import com.vtes.service.FileDataService;

@RestController
@RequestMapping("/api/v1/files")
public class FileApiController {

	@Autowired
	private FileDataService fileService;
	
	@GetMapping
	public ResponseEntity<?> getExportedFiles(){
		Integer userId = getAuthenticatedUserId();
		List<FileData> files = fileService.findByUserId(userId);
		return ResponseEntity.ok()
				.body(ResponseData.builder()
						.code("")
						.message("Success")
						.type(ResponseType.INFO)
						.data(files)
						.build());
		
	}

	@PostMapping( produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, UploadFileException {

		// upload file to S3 and save file data to DB
		fileService.uploadFileToS3(getAuthenticatedUserId(), file);

		return ResponseEntity.ok()
				.body(ResponseData.builder().code("")
						.type(ResponseType.INFO)
						.message("File uploaded").build());

	}

	@GetMapping(value = "/{fileId}")
	public ResponseEntity<?> downloadFile(@PathVariable(value = "fileId", required = true) Integer fileId)
			throws IOException, NotFoundException {

		Integer userId = getAuthenticatedUserId();
		byte[] data = fileService.download(fileId, userId);
		final ByteArrayResource resource = new ByteArrayResource(data);
		String fileName = URLEncoder.encode(fileService.getFileNameById(fileId), "UTF-8");
		String contentDiposition = "attachment; filename=\"" + fileName + "\"";

		return ResponseEntity.ok().contentLength(data.length).header(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
				.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDiposition).body(resource);

	}

	private Integer getAuthenticatedUserId() {
		// Get authenticated user from security context
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
		return userDetails.getId();
	}
}
