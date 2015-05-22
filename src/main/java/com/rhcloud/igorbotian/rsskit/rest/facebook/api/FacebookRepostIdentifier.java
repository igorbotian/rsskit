package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class FacebookRepostIdentifier {

    private static final PostIDIdentifier BY_ID = new IDBasedPostIDIdentifier();
    private static final PostIDIdentifier BY_OBJECT_ID = new ObjectIDBasedPostIDIdentifier();
    private static final SourceSelector OLDEST_POST_AS_SOURCE = new OldestPostAsSourceSelector();

    public List<IncompleteFacebookPost> apply(List<IncompleteFacebookPost> posts) {
        Objects.requireNonNull(posts);

        return identifyByObjectID(identifyByPostID(posts));
    }

    private List<IncompleteFacebookPost> identifyByPostID(List<IncompleteFacebookPost> posts) {
        assert posts != null;
        return apply(posts, BY_ID, OLDEST_POST_AS_SOURCE);
    }

    private List<IncompleteFacebookPost> identifyByObjectID(List<IncompleteFacebookPost> posts) {
        assert posts != null;

        List<IncompleteFacebookPost> filtered = new ArrayList<>(posts.size());
        List<IncompleteFacebookPost> withObjectID = new ArrayList<>();

        for (IncompleteFacebookPost post : posts) {
            if (post.objectID != null) {
                withObjectID.add(post);
            } else {
                filtered.add(post);
            }
        }

        filtered.addAll(apply(withObjectID, BY_OBJECT_ID, OLDEST_POST_AS_SOURCE));

        return filtered;
    }

    private List<IncompleteFacebookPost> apply(List<IncompleteFacebookPost> posts, PostIDIdentifier idIdentifier,
                                               SourceSelector sourceSelector) {
        assert posts != null;
        assert idIdentifier != null;
        assert sourceSelector != null;

        List<IncompleteFacebookPost> filtered = new ArrayList<>(posts.size());
        Map<String, Set<IncompleteFacebookPost>> groups = groupPosts(posts, idIdentifier);

        for (Map.Entry<String, Set<IncompleteFacebookPost>> group : groups.entrySet()) {
            Set<IncompleteFacebookPost> items = group.getValue();
            assert !items.isEmpty();

            if (items.size() > 1) {
                IncompleteFacebookPost source = sourceSelector.select(items);

                for (IncompleteFacebookPost item : items) {
                    if (source != item) {
                        item.source = source;
                        filtered.add(item);
                    }
                }
            } else {
                filtered.add(items.iterator().next());
            }
        }

        return filtered;
    }

    private Map<String, Set<IncompleteFacebookPost>> groupPosts(List<IncompleteFacebookPost> posts,
                                                                PostIDIdentifier idIdentifier) {
        assert posts != null;
        assert idIdentifier != null;

        Map<String, Set<IncompleteFacebookPost>> groups = new LinkedHashMap<>();

        for (IncompleteFacebookPost post : posts) {
            String id = idIdentifier.getID(post);

            if (!groups.containsKey(id)) {
                groups.put(id, new HashSet<IncompleteFacebookPost>());
            }

            groups.get(id).add(post);
        }

        return groups;
    }

    //-------------------------------------------------------------------------

    private interface SourceSelector {

        IncompleteFacebookPost select(Set<IncompleteFacebookPost> posts);
    }

    private static class OldestPostAsSourceSelector implements SourceSelector {

        @Override
        public IncompleteFacebookPost select(Set<IncompleteFacebookPost> posts) {
            assert posts != null;

            Iterator<IncompleteFacebookPost> it = posts.iterator();
            IncompleteFacebookPost oldest = it.next();

            while (it.hasNext()) {
                IncompleteFacebookPost post = it.next();

                if (post.createdTime.before(oldest.createdTime)) {
                    oldest = post;
                }
            }

            return oldest;
        }
    }

    //-------------------------------------------------------------------------

    private interface PostIDIdentifier {

        String getID(IncompleteFacebookPost post);
    }

    private static class IDBasedPostIDIdentifier implements PostIDIdentifier {

        @Override
        public String getID(IncompleteFacebookPost post) {
            assert post != null;

            String[] ids = StringUtils.split(post.id, "_");
            return ids.length > 0 ? ids[ids.length - 1] : post.id;
        }
    }

    private static class ObjectIDBasedPostIDIdentifier implements PostIDIdentifier {

        @Override
        public String getID(IncompleteFacebookPost post) {
            assert post != null;
            return post.objectID;
        }
    }
}
