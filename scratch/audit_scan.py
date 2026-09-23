import json
import re
from urllib.parse import urlparse
from collections import Counter, defaultdict

with open('src/main/resources/aws_api_final.json', 'r', encoding='utf-8') as f:
    raw_content = f.read()
    data = json.loads(raw_content)

print(f"Total raw size: {len(raw_content)} bytes")
print(f"Total root keys: {list(data.keys())}")

# 1. Sensitive Patterns
sensitive_patterns = {
    'AWS Access Key': r'AKIA[0-9A-Z]{16}',
    'AWS Secret Key': r'(?i)aws_secret_access_key',
    'Password assignment': r'(?i)password\s*[:=]\s*["\'][^"\']+["\']',
    'Private Key': r'-----BEGIN (RSA|EC|DSA|OPENSSH|PRIVATE) KEY-----',
    'Localhost / Loopback': r'(?i)\b(localhost|127\.0\.0\.1|0\.0\.0\.0)\b',
    'Private RFC1918 IP': r'\b(10\.\d{1,3}\.\d{1,3}\.\d{1,3}|172\.(1[6-9]|2\d|3[01])\.\d{1,3}\.\d{1,3}|192\.168\.\d{1,3}\.\d{1,3})\b',
    'Local Windows Path': r'[a-zA-Z]:\\[a-zA-Z0-9_]+',
    'Local Unix Path': r'\b(\/home\/|\/Users\/|\/etc\/|\/tmp\/|\/var\/)',
    'Internal Domains': r'(?i)\b[a-z0-9-]+\.(internal|corp|local)\b',
}

print('=== SENSITIVE DATA SCAN ===')
for name, pattern in sensitive_patterns.items():
    matches = re.findall(pattern, raw_content)
    print(f'{name}: {len(matches)} matches')
    if matches:
        print(f'   Sample: {matches[:3]}')

# 2. Injection Patterns
injection_patterns = {
    'HTML Tag': r'<[a-zA-Z/][^>]*>',
    'Script Tag / JS': r'(?i)(<script|javascript:|eval\(|onload=)',
    'SQL Injection': r'(?i)(\' OR 1=1|\; DROP TABLE|\bUNION SELECT\b)',
    'Template Injection': r'(\{\{.*?\}\}|\$\{.*?\})',
    'Unescaped Control Chars': r'[\x00-\x08\x0B\x0C\x0E-\x1F]'
}

print('\n=== INJECTION & SANITIZATION SCAN ===')
for name, pattern in injection_patterns.items():
    matches = re.findall(pattern, raw_content)
    print(f'{name}: {len(matches)} matches')
    if matches:
        print(f'   Sample: {matches[:5]}')
