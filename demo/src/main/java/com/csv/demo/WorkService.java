package com.csv.demo;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkService{
    private final WorkRepository workRepository;

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public List<Work> saveCsvFileToDb(MultipartFile file) throws IOException {
        Set<Work> works = parseCsvFile(file);

        workRepository.saveAll(works);

        return new ArrayList<>(works);
    }

    private Set<Work> parseCsvFile(MultipartFile file) throws IOException {
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            HeaderColumnNameMappingStrategy<Work> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Work.class);

            CsvToBean<Work> csvToBean = new CsvToBeanBuilder<Work>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            return csvToBean.parse()
                    .stream()
                    .map(lineWork -> Work.builder()
                            .name(lineWork.getName())
                            .description(lineWork.getDescription())
                            .location(lineWork.getLocation())
                            .build()).collect(Collectors.toSet());
        }


    }
}
