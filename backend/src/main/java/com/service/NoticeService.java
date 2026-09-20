package com.service;

import com.entity.Notice;
import com.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id).orElse(null);
    }

    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Notice updateNotice(Long id, Notice noticeDetails) {
        Notice existing = noticeRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setTitle(noticeDetails.getTitle());
        existing.setContent(noticeDetails.getContent());
        existing.setAuthor(noticeDetails.getAuthor());
        existing.setPublishedAt(noticeDetails.getPublishedAt());
        existing.setCategory(noticeDetails.getCategory());

        return noticeRepository.save(existing);
    }

    public boolean deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            return false;
        }
        noticeRepository.deleteById(id);
        return true;
    }
}
