import json
from collections import Counter

with open('src/main/resources/aws_api_final.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

results = data['results']

# 1. Date formats
date_formats = Counter()
for s in results:
    date_formats[s['metadata']['updated_at']] += 1
print('Metadata updated_at values/formats:', dict(date_formats))
print('Info updated_at:', data['info']['updated_at'])

# 2. Check for null values, empty strings, empty arrays across all fields
null_counts = Counter()
empty_str_counts = Counter()
empty_list_counts = Counter()

def check_obj(prefix, obj):
    if isinstance(obj, dict):
        for k, v in obj.items():
            full_k = f"{prefix}.{k}" if prefix else k
            if v is None:
                null_counts[full_k] += 1
            elif isinstance(v, str) and v.strip() == "":
                empty_str_counts[full_k] += 1
            elif isinstance(v, list) and len(v) == 0:
                empty_list_counts[full_k] += 1
            elif isinstance(v, (dict, list)):
                check_obj(full_k, v)
    elif isinstance(obj, list):
        for item in obj:
            if isinstance(item, (dict, list)):
                check_obj(prefix, item)

for s in results:
    check_obj("service", s)

print("\nNull counts across services:", dict(null_counts))
print("Empty string counts across services:", dict(empty_str_counts))
print("Empty list counts across services:", dict(empty_list_counts))

# 3. Tags audit
all_tags = Counter()
tag_counts_per_service = Counter()
for s in results:
    tags = s['metadata']['tags']
    tag_counts_per_service[len(tags)] += 1
    for t in tags:
        all_tags[t] += 1

print(f"\nDistinct tags: {len(all_tags)}")
print(f"Tags count per service distribution: {dict(tag_counts_per_service)}")
print(f"Top 15 most frequent tags: {all_tags.most_common(15)}")
