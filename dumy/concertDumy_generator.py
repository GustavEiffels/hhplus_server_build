import csv
import random
from datetime import datetime, timedelta
from faker import Faker

# Faker 초기화
fake = Faker()

# CSV 파일 생성
concert_file_name = "concert_data.csv"
schedule_file_name = "concert_schedule_data.csv"
seat_file_name = "seat_data.csv"

# 기본 설정
concert_count = 1000000  # 콘서트 개수
schedule_count = 1000000  # 콘서트 일정 개수
seats_per_schedule = 50  # 각 일정마다 좌석 수

# # # 콘서트 데이터 생성
with open(concert_file_name, "w", newline="", encoding="utf-8") as concert_file:
    concert_writer = csv.writer(concert_file)
    concert_writer.writerow(["id", "performer", "title", "create_at", "update_at"])

    for i in range(concert_count):
        concert_data = [
            i + 1,  # id
            fake.name(),  # performer
            fake.sentence(),  # title
            datetime.now().strftime("%Y-%m-%d %H:%M:%S"),  # create_at
            datetime.now().strftime("%Y-%m-%d %H:%M:%S")  # update_at
        ]
        concert_writer.writerow(concert_data)

# 콘서트 일정 데이터 생성
schedule_id_counter = 1  # 유니크한 schedule_id를 위한 전역 카운터

with open(schedule_file_name, "w", newline="", encoding="utf-8") as schedule_file:
    schedule_writer = csv.writer(schedule_file)
    schedule_writer.writerow(["concert_id", "is_reserve_able", "create_at", "reservation_start", "reservation_end", "schedule_id", "show_time", "update_at"])

    for concert_id in range(1, concert_count + 1):  # 각 콘서트 ID에 대해
        num_schedules = 10  # 콘서트당 최대 10개의 스케줄

        for _ in range(num_schedules):
            concert_schedule_data = [
                concert_id,  # 해당 콘서트의 id
                random.choice([0, 1]),  # is_reserve_able (예약 가능 여부)
                datetime.now().strftime("%Y-%m-%d %H:%M:%S"),  # create_at
                (datetime.now() + timedelta(days=random.randint(-15, 15))).strftime("%Y-%m-%d %H:%M:%S"),  # reservation_start
                (datetime.now() + timedelta(days=random.randint(15, 20))).strftime("%Y-%m-%d %H:%M:%S"),  # reservation_end
                schedule_id_counter,  # 유니크한 schedule_id 사용
                (datetime.now() + timedelta(hours=random.randint(20, 30))).strftime("%Y-%m-%d %H:%M:%S"),  # show_time
                datetime.now().strftime("%Y-%m-%d %H:%M:%S")  # update_at
            ]
            schedule_writer.writerow(concert_schedule_data)
            schedule_id_counter += 1  # 다음 스케줄 ID로 증가



# # 좌석 데이터 생성
with open(seat_file_name, "w", newline="", encoding="utf-8") as seat_file:
    seat_writer = csv.writer(seat_file)
    seat_writer.writerow(["seat_no", "status", "schedule_id", "price", "seat_id", "create_at", "update_at"])

    for i in range(schedule_count):
        for j in range(seats_per_schedule):
            seat_data = [
                j + 1,  # seat_no (좌석 번호는 각 일정마다 1부터 시작)
                random.choice([0, 1]),  # status (0 or 1) 0 : RESERVED, 1: RESERVABLE
                i + 1,  # schedule_id (외래키)
                random.randint(1000, 5000),  # price
                i * seats_per_schedule + j + 1,  # seat_id (Primary Key)
                datetime.now().strftime("%Y-%m-%d %H:%M:%S"),  # create_at
                datetime.now().strftime("%Y-%m-%d %H:%M:%S")  # update_at
            ]
            seat_writer.writerow(seat_data)

print(f"{concert_file_name}, {schedule_file_name}, {seat_file_name} 파일이 성공적으로 생성됨!")
