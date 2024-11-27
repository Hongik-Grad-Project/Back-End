package trackers.demo.gallery.domain.repository;

import trackers.demo.project.domain.Tag;

import java.util.List;

public interface CustomProjectTagRepository {

    List<Tag> findPopularTagsByCount(final int count);
}
