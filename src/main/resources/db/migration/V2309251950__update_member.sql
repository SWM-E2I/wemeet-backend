ALTER TABLE `member`
    ADD `recommender_phone` char(24) default null AFTER `deleted_at`;