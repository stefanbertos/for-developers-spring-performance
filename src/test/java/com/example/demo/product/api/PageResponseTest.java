package com.example.demo.product.api;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResponseTest {

    @Test
    void from_ShouldCreatePageResponseFromPage() {
        // Arrange
        List<String> content = List.of("item1", "item2", "item3");
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(content, pageable, content.size());

        // Act
        PageResponse<String> pageResponse = PageResponse.from(page);

        // Assert
        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.content()).isEqualTo(content);
        assertThat(pageResponse.page()).isEqualTo(page.getNumber());
        assertThat(pageResponse.size()).isEqualTo(page.getSize());
        assertThat(pageResponse.totalElements()).isEqualTo(page.getTotalElements());
        assertThat(pageResponse.totalPages()).isEqualTo(page.getTotalPages());
        assertThat(pageResponse.first()).isEqualTo(page.isFirst());
        assertThat(pageResponse.last()).isEqualTo(page.isLast());
    }
}