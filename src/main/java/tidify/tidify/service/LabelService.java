package tidify.tidify.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tidify.tidify.dto.LabelResponse;
import tidify.tidify.repository.LabelRepository;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    @Cacheable(value = "label")
    public List<LabelResponse> getAllLabel() {
        return labelRepository.findAll()
            .stream()
            .map(LabelResponse::of)
            .collect(toList());
    }
}
