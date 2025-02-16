import csv
import random
from faker import Faker

# Faker 초기화
fake = Faker()

# CSV 파일 생성
dept_file_name = "department_data.csv"

# 부서 개수
num_departments = 100  

# 데이터 생성 및 CSV 저장
with open(dept_file_name, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)

    # 헤더 작성
    writer.writerow(["dept_id", "dept_name"])

    # 부서 100개 생성
    for dept_id in range(1, num_departments + 1):
        dept_name = fake.company()  # 랜덤한 회사 이름을 부서명으로 사용
        writer.writerow([dept_id, dept_name])

print(f"{dept_file_name} 파일이 성공적으로 생성됨!")

