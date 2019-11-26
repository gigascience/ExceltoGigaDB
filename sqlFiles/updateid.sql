select setval('attribute_id_seq',(select max(id) from attribute));
select setval('dataset_attributes_id_seq',(select max(id) from dataset_attributes));
select setval('author_id_seq',(select max(id) from author));
select setval('dataset_funder_id_seq',(select max(id) from dataset_funder));
select setval('dataset_author_id_seq',(select max(id) from dataset_author));
select setval('dataset_log_id_seq',(select max(id) from dataset_log));
select setval('dataset_project_id_seq',(select max(id) from dataset_project ));
select setval('dataset_sample_id_seq',(select max(id) from dataset_sample ));
select setval('dataset_type_id_seq',(select max(id) from dataset_type ));
select setval('dataset_id_seq',(select max(id) from dataset));
select setval('external_link_id_seq',(select max(id) from external_link ));
select setval('exp_attributes_id_seq',(select max(id) from exp_attributes));
select setval('experiment_id_seq',(select max(id) from experiment));
select setval('extdb_id_seq',(select max(id) from extdb));
select setval('external_link_id_seq',(select max(id) from external_link));
select setval('external_link_type_id_seq',(select max(id) from external_link_type));
select setval('file_attributes_id_seq',(select max(id) from file_attributes));
select setval('file_experiment_id_seq',(select max(id) from file_experiment));
select setval('file_relationship_id_seq',(select max(id) from file_relationship));
select setval('file_sample_id_seq',(select max(id) from file_sample));
select setval('file_id_seq',(select max(id) from file ));
select setval('file_type_id_seq',(select max(id) from file_type ));
select setval('funder_name_id_seq',(select max(id) from funder_name));
select setval('file_format_id_seq',(select max(id) from file_format ));
select setval('image_id_seq',(select max(id) from image ));
select setval('link_id_seq',(select max(id) from link ));
select setval('link_prefix_id_seq',(select max(id) from prefix ));
select setval('manuscript_id_seq',(select max(id) from manuscript ));
select setval('relation_id_seq',(select max(id) from relation ));
select setval('relationship_id_seq',(select max(id) from relationship ));
select setval('sample_id_seq',(select max(id) from sample ));
select setval('sample_attribute_id_seq',(select max(id) from sample_attribute));
select setval('sample_experiment_id_seq',(select max(id) from sample_experiment ));
select setval('sample_rel_id_seq',(select max(id) from sample_rel));
select setval('species_id_seq',(select max(id) from species ));
select setval('project_id_seq',(select max(id) from project ));
select setval('dataset_project_id_seq',(select max(id) from dataset_project ));
select setval('gigadb_user_id_seq',(select max(id) from gigadb_user ));
select setval('type_id_seq',(select max(id) from type ));
select setval('curation_log_id_seq',(select max(id) from curation_log ));




