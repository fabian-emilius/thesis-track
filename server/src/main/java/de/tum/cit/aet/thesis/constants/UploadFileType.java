package de.tum.cit.aet.thesis.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UploadFileType {
    PDF("PDF"),
    IMAGE("IMAGE"),
    GROUP_LOGO("GROUP_LOGO"),
    ANY("ANY");

    private final String value;
}
