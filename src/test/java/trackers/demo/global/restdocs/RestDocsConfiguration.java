package trackers.demo.global.restdocs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Attributes.Attribute;

@Configuration
public class RestDocsConfiguration {

    public static Attribute field(final String key, final String value){
        return new Attribute(key, value);
    }

    @Bean
    public RestDocumentationResultHandler write(){
        return MockMvcRestDocumentation.document(
                "{class-name}/{method-name}",   // snippet이 생성되는 디렉토리 경로명
                Preprocessors.preprocessRequest(
                        Preprocessors.modifyHeaders()
                                .remove("Content-Length")
                                .remove("Host"),
                        Preprocessors.prettyPrint()
                ),
                Preprocessors.preprocessResponse(
                        Preprocessors.modifyHeaders()
                                .remove("Transfer-Encoding")
                                .remove("Date")
                                .remove("Keep-Alive")
                                .remove("Connection"),
                        Preprocessors.prettyPrint()
                )
        );
    }
}
