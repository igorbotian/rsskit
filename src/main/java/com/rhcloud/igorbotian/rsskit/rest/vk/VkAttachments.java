package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkAttachments {

    private static final VkEntityParser<VkAttachments> PARSER = new VkAttachmentsParser();

    public final List<VkPhoto> photos;
    public final List<VkPostedPhoto> postedPhotos;
    public final List<VkVideo> videos;
    public final List<VkAudio> audios;
    public final List<VkDocument> documents;
    public final List<VkLink> links;
    public final List<VkNote> notes;
    public final List<VkPage> pages;

    public VkAttachments() {
        this(
                Collections.<VkAudio>emptyList(),
                Collections.<VkVideo>emptyList(),
                Collections.<VkDocument>emptyList(),
                Collections.<VkLink>emptyList(),
                Collections.<VkPage>emptyList(),
                Collections.<VkPhoto>emptyList(),
                Collections.<VkPostedPhoto>emptyList(),
                Collections.<VkNote>emptyList()
        );
    }

    public VkAttachments(List<VkAudio> audios, List<VkVideo> videos, List<VkDocument> documents,
                         List<VkLink> links, List<VkPage> pages, List<VkPhoto> photos,
                         List<VkPostedPhoto> postedPhotos, List<VkNote> notes) {

        this.audios = Collections.unmodifiableList(Objects.requireNonNull(audios));
        this.videos = Collections.unmodifiableList(Objects.requireNonNull(videos));
        this.documents = Collections.unmodifiableList(Objects.requireNonNull(documents));
        this.links = Collections.unmodifiableList(Objects.requireNonNull(links));
        this.pages = Collections.unmodifiableList(Objects.requireNonNull(pages));
        this.photos = Collections.unmodifiableList(Objects.requireNonNull(photos));
        this.postedPhotos = Collections.unmodifiableList(Objects.requireNonNull(postedPhotos));
        this.notes = Collections.unmodifiableList(Objects.requireNonNull(notes));
    }

    public static VkAttachments parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkAttachmentsParser extends VkEntityParser<VkAttachments> {

        @Override
        public VkAttachments parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            List<VkAudio> audios = new ArrayList<>();
            List<VkVideo> videos = new ArrayList<>();
            List<VkDocument> documents = new ArrayList<>();
            List<VkLink> links = new ArrayList<>();
            List<VkPage> pages = new ArrayList<>();
            List<VkPhoto> photos = new ArrayList<>();
            List<VkPostedPhoto> postedPhotos = new ArrayList<>();
            List<VkNote> notes = new ArrayList<>();

            for (int i = 0; i < json.size(); i++) {
                JsonNode attachment = json.get(i);
                String type = getAttribute(attachment, "type").asText();

                if ("photo".equals(type)) {
                    photos.add(VkPhoto.parse(getAttribute(attachment, "photo")));
                } else if ("posted_photo".equals(type)) {
                    postedPhotos.add(VkPostedPhoto.parse(getAttribute(attachment, "posted_photo")));
                } else if ("video".equals(type)) {
                    videos.add(VkVideo.parse(getAttribute(attachment, "video")));
                } else if ("audio".equals(type)) {
                    audios.add(VkAudio.parse(getAttribute(attachment, "audio")));
                } else if ("doc".equals(type)) {
                    documents.add(VkDocument.parse(getAttribute(attachment, "doc")));
                } else if ("link".equals(type)) {
                    links.add(VkLink.parse(getAttribute(attachment, "link")));
                } else if ("page".equals(type)) {
                    pages.add(VkPage.parse(getAttribute(attachment, "page")));
                } else if ("note".equals(type)) {
                    notes.add(VkNote.parse(getAttribute(attachment, "note")));
                }
            }

            return new VkAttachments(audios, videos, documents, links, pages, photos, postedPhotos, notes);
        }
    }
}
