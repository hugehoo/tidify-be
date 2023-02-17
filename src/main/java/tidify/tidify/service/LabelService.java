package tidify.tidify.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tidify.tidify.domain.Label;
import tidify.tidify.dto.LabelResponse;
import tidify.tidify.repository.LabelRepository;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;

    public List<LabelResponse> getAllLabel() {
        List<Label> labels = labelRepository.findAll();
        return labels.stream().map(LabelResponse::of).collect(Collectors.toList());
    }
}
