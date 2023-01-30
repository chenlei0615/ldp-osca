package com.longi.msp.osca.constants;

import com.meicloud.paas.core.preview.TxtStreamReader;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;

import java.io.InputStream;

/**
 * @author chenlei140
 * @className DocumentFormatEnum
 * @description 文档枚举
 * @date 2021/11/30 11:28
 */
public enum DocumentFormatEnum {
    PNG {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PNG;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.PNG;
        }
    },
    JPG {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.JPEG;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.JPEG;
        }
    },
    JPEG {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.JPEG;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.JPEG;
        }
    },
    GIF {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.GIF;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.GIF;
        }
    },
    DOC {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.DOC;
        }
    },
    DOCX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.DOCX;
        }
    },
    PDF {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PDF;
        }
    },
    PPT {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PPT;
        }
    },
    PPTX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.PPTX;
        }
    },
    XLS {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.XLS;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }

    },
    XLSX {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.XLSX;
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }

    },

    TXT {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.TXT;
        }

        @Override
        public InputStream getInputStream(InputStream inputStream) {
            return TxtStreamReader.getInputStream(inputStream);
        }
    },

    CSV {
        @Override
        public DocumentFormat getFormFormat() {
            return DefaultDocumentFormatRegistry.CSV;
        }

        @Override
        public InputStream getInputStream(InputStream inputStream) {
            return TxtStreamReader.getInputStream(inputStream);
        }

        @Override
        public DocumentFormat getTargetFormat() {
            return DefaultDocumentFormatRegistry.HTML;
        }
    };

    public InputStream getInputStream(InputStream inputStream) {
        return inputStream;
    }

    public abstract DocumentFormat getFormFormat();

    public DocumentFormat getTargetFormat() {
        return DefaultDocumentFormatRegistry.PDF;
    }
}
