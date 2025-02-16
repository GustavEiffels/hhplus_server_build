import csv
import random
from datetime import datetime
from faker import Faker

# Faker 초기화
fake = Faker()

# CSV 파일 생성
file_name = "employee_data.csv"

# 전공 과목 리스트
majors = ["Computer Science", "Mathematics", "Physics", "Biology", "Chemistry",
          "History", "Literature", "Economics", "Engineering", "Psychology"]

# 성별 리스트
genders = ["Male", "Female"]

# 부서 개수 (100개)
num_departments = 100  

# 랜덤 생년월일 생성 함수 (20~50세)
def random_birthdate():
    age = random.randint(20, 50)
    birth_year = datetime.now().year - age
    birth_date = datetime(birth_year, random.randint(1, 12), random.randint(1, 28))
    return birth_date.strftime("%Y-%m-%d")  # YYYY-MM-DD 형식

# 데이터 생성 및 CSV 저장
with open(file_name, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)

    # 헤더 작성
    writer.writerow(["id", "name", "gender", "age", "birthdate", "major", "city", "salary", "wealth", "dept_id"])

    # 데이터 10,000,000개 생성
    for i in range(1, 10_000_001):
        name = fake.name() 
        gender = random.choice(genders)  # 성별 랜덤 선택
        age = random.randint(20, 50)    # 나이
        birthdate = random_birthdate()  # 생년월일
        major = random.choice(majors)  # 전공
        city = fake.city()  # 랜덤 도시
        salary = random.randint(30_000, 150_000)  # 연봉 (30,000 ~ 150,000)
        wealth = random.randint(1_000, 1_000_000)  # 자산 (1,000 ~ 1,000,000)
        dept_id = random.randint(1, num_departments)  # 부서 ID (1~100)

        # CSV에 행 추가
        writer.writerow([i, name, gender, age, birthdate, major, city, salary, wealth, dept_id])

print(f"{file_name} 파일이 성공적으로 생성됨!")
