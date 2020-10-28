package com.example.qbs;

import com.example.qbs.exceptons.FileDoesntContainThisByteCode;
import com.example.qbs.exceptons.FileWithThisExtensionDoesntExist;
import com.example.qbs.exceptons.SameByteCodesException;
import com.example.qbs.exceptons.WrongFormatByteCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public String get() {
        return "index";
    }

    @PostMapping
    public String post(@RequestParam String path,
                       @RequestParam String extension,
                       @RequestParam String bytesToRemove,
                       @RequestParam String bytesToAdd,
                       ModelMap map) {
        String message = null;
        try {
            message = fileService.changeByteArrayInFiles(path, extension, bytesToRemove, bytesToAdd);
        } catch (WrongFormatByteCodeException e) {
            message = e.getMessage();
        } catch (SameByteCodesException e) {
            message = e.getMessage();
        } catch (FileWithThisExtensionDoesntExist e) {
            message = e.getMessage();
        } catch (FileDoesntContainThisByteCode e) {
            message = e.getMessage();
        }
        map.put("userMessage", message);
//      map.put("userMessage", path + "/" + extension + "/" + bytesToRemove + "/" + bytesToAdd);
        map.put("path", path);
        map.put("extension", extension);
        map.put("bytesToRemove", bytesToRemove);
        map.put("bytesToAdd", bytesToAdd);
        return "index";
    }

}
