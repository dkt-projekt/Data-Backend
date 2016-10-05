package de.dkt.eservices.databackend.collectionexploration.authorities;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.dkt.eservices.databackend.common.DataBackendCommonConfig;

@Configuration
@ComponentScan
@Import(DataBackendCommonConfig.class)
public class FindAuthoritiesConfiguration {

}
