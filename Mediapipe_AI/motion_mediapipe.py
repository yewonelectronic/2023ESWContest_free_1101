#-*-coding:utf-8 -*-
import cv2
import mediapipe as mp
import numpy as np

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# mediapipe 초기화
mp_drawing = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles
mp_pose = mp.solutions.pose
# Firebase 초기화
cred = credentials.Certificate('android2-a0742-firebase-adminsdk-iyxho-064169b4e7.json')
firebase_admin.initialize_app(cred,{'databaseURL':'https://android2-a0742-default-rtdb.firebaseio.com/'})

dir=db.reference()

# 반복문 내에서 한 번만 실행하기 위해 선언한 전역변수
once_inside = False
once_flipped = False


#################### 함수 1 , 다각형을 그리는 함수
def drawPoly(image,polygon,color) :

    # 매개변수 : 1. image : 화면, 2. polygon : 다각형 점 4개 -> 리스트 형식으로 집어넣을 것 3. color : 색깔
    
    ## 매개변수 끝
    
    # 사각형 좌표를 입력받고 사각형을 그립니다.
    points = np.array(polygon,np.int32)
    cv2.polylines(image, [points], True, color, 2)

    return

    ## 함수 1 끝

###################### 함수 2 : 다각형 내에 점이 존재하는지 체크하는 함수
def isInside(image, polygon, point1, point2) :
    # 매개변수 : 1. image : 화면, 2. polygon : 다각형 점 4개 -> 리스트 형식으로 집어넣을 것 3. point1, point2 : mediapipe의 점
    
    ## 매개변수 끝

    global once_inside
    # 전역변수 : 1.once_inside : 무한 반복문 내에서 firebase 값이 계속 전송되면 FPS가 급격하게 감소하기 떄문에,
    # firebase 값이 단 한번만 전달 되도록 조건문을 설정할 예정이다.

    ## 전역변수 끝

    ## 침대의 좌상, 좌하, 우하, 우상 점 설정
    p1 = polygon[0]
    p2 = polygon[1]
    p3 = polygon[2]
    p4 = polygon[3]

    ## real_x, real_y : 판별의 대상이 되는 점
        # mediapipe의 좌표 값은 화면의 비율에 비례해서 0에서 1 사이 값으로 나온다.
        # 따라서 화면 전체 값을 곱하여 실제 화면상의 좌표를 구할 수 있다.
    real_x = point1.x * image.shape[1] / 2 + point2.x * image.shape[1] / 2 
    real_y = point1.y * image.shape[0] / 2 + point2.y * image.shape[0] / 2  

    ## real_x, real_y 가 사각형 안에 있는지 확인하기 위한 과정이다.
        # real_x 가 xinters_l 과 xinters_r 의 사이에 있는지
        # real_y 가 yinters_d 와 yinters_u 의 사이에 있는지 확인하기 위해 좌표 값을 구하는 과정이다.
    yinters_u = (real_x - p1[0]) * (p4[1] - p1[1]) / (p4[0] - p1[0]) + p1[1] # 좌상 우상 점을 이은 직선의 방정식
    yinters_d = (real_x - p2[0]) * (p3[1] - p2[1]) / (p3[0] - p2[0]) + p2[1] # 좌하 우하 점을 이은 직선의 방정식
    xinters_l = (real_y - p1[1]) * (p2[0] - p1[0]) / (p2[1]-p1[1]) + p1[0] # 좌상 좌하 점을 이은 직선의 방정식
    xinters_r = (real_y - p4[1]) * (p3[0] - p4[0]) / (p3[1]-p4[1]) + p4[0] # 우상 우하 점을 이은 직선의 방정식

        # 화면상에 좌표를 표시하기 위해 원 그리기
    #cv2.circle(image,[int(xinters_l),int(real_y)],5,(255,0,0),-1)
    #cv2.circle(image,[int(xinters_r),int(real_y)],5,(0,255,0),-1)
    #cv2.circle(image,[int(real_x),int(yinters_u)],5,(125,125,0),-1)
    #cv2.circle(image,[int(real_x),int(yinters_d)],5,(0,0,255),-1)

    cv2.circle(image,[int(real_x),int(real_y)],5,(0,0,0),-1)

        # 확인한 후 사각형 내부에 있으면 이 함수는 False 를 리턴하고
        # 사각형 외부에 있으면 이 함수는 True 를 리턴한다.
    if int(xinters_l) <= int(real_x) and int(real_x) <= int(xinters_r) and int(yinters_d) >= int(real_y) and int(real_y) >= int(yinters_u):
        return False
    else :
        return True

    ## 함수 2 끝

############# 함수 3 : 뒤집어졌는지 확인하는 함수, 입력 : 이미지, mediapose로 계산된 결과 리스트
def isFlipped(image, results) :

    # 매개변수 : 1. image : 화면, 2. results : mediapipe의 연산결과가 담긴 객체
    
    ## 매개변수 끝
    
    marks = results.pose_landmarks.landmark # using namespace
    distance = marks[mp_pose.PoseLandmark.NOSE].x * image.shape[1] - marks[mp_pose.PoseLandmark.LEFT_EYE_INNER].x * image.shape[1]
    # 코와 왼쪽 눈 안쪽 점의 x 좌표를 뺀다

    # 주의 : 화면은 좌우 대칭이기 때문에 헷갈릴 수 있음
    # 코의 x 좌표에서 왼쪽 눈 바깥 x 좌표를 빼면 음수 값이 나온다.
    distance_left = marks[mp_pose.PoseLandmark.NOSE].x * image.shape[1] - marks[mp_pose.PoseLandmark.LEFT_EYE_OUTER].x * image.shape[1]
    # 코의 x 좌표에서 오른쪽 눈 바깥 x 좌표를 빼면 양수 값이 나온다.
    distance_right = marks[mp_pose.PoseLandmark.NOSE].x * image.shape[1] - marks[mp_pose.PoseLandmark.RIGHT_EYE_OUTER].x * image.shape[1]

    # 고개를 왼쪽으로 돌리면 distance_left 는 양수가 되고, 고개를 오른쪽으로 돌리면 distance_right 는 음수가 된다. 임계값을 넘어가면 알림이 가도록 만드는 과정이다.
    if distance_left > 10 or distance_right < -10:
        return True
    else :
        return False
    
    ## 함수 3 끝

####################### Main #############################
def main() :
    global once_flipped

    back_off = 1
    fall_off = 1
    back = 0
    fall = 0
    night = 0
    count = 0

    fall_count = 0 # 낙상인지 확인하는 변수. 낙상이라고 판단되면 1씩 더해져서 100이 되면 알림이 울림.

    # 전역변수 : once_flipped -> 뒤집어졌을 때 firebase로 값을 한 번만 보내도록 설정한 변수이다.

    with mp_pose.Pose(
            min_detection_confidence=0.5,
            min_tracking_confidence=0.5) as pose:

        phone_width = 1074
        phone_height = 816

    # 사각형 꼭짓점 좌표를 불러오는 부분
        dir = db.reference('BED')
        bed = dir.get()
        x1 = bed['X1'] * 1280 / phone_width
        x2 = bed['X2'] * 1280 / phone_width
        x3 = bed['X3'] * 1280 / phone_width
        x4 = bed['X4'] * 1280 / phone_width

        y1 = (bed['Y1']-75) * 720 / phone_height 
        y2 = (bed['Y2']-75) * 720 / phone_height 
        y3 = (bed['Y3']-75) * 720 / phone_height 
        y4 = (bed['Y4']-75) * 720 / phone_height 

        polygon = [(int(x1),int(y1)),(int(x2),int(y2)),(int(x3),int(y3)),(int(x4),int(y4))]
        print(polygon)

        if night == 0 :
            cap = cv2.VideoCapture(0)
        if night == 1 :
            cap = cv2.VideoCapture(1)
        cap.set(3,1280) # 가로 설정
        cap.set(4,720) # 세로 설정

        #################3
        while cap.isOpened():
            success, image = cap.read()
            image_height, image_width, _ = image.shape
            if not success:
                print("카메라를 찾을 수 없습니다.")
                continue
            print("polygon :" , polygon)
            ###################### 4초 #########################
            count += 1
            ###################### 1초 : 침대의 X 좌표를 불러옴 #####
            if count == 60 :
                dir = db.reference('BED')
                bed = dir.get()
                x1 = bed['X1'] * 1280 / phone_width
                x2 = bed['X2'] * 1280 / phone_width
                x3 = bed['X3'] * 1280 / phone_width
                x4 = bed['X4'] * 1280 / phone_width

            
            ###################### 2초 : 침대의 Y 좌표를 불러옴 #####
            if count == 120 :
                y1 = (bed['Y1']-75) * 720 / phone_height 
                y2 = (bed['Y2']-75) * 720 / phone_height 
                y3 = (bed['Y3']-75) * 720 / phone_height 
                y4 = (bed['Y4']-75) * 720 / phone_height 

            ###################### 3초 : 침대의 테두리 다시 그림 #####
            if count == 180 :
                # 침대의 좌상, 좌하, 우하, 우상 점 설정
                polygon = [(int(x1),int(y1)),(int(x2),int(y2)),(int(x3),int(y3)),(int(x4),int(y4))]
                #함수 호출

            ##################### 4초 : 모드가 바뀌었는지 체크함 #####
            if count == 240 :
                dir2 = db.reference('ONOFF')
                bpm = dir2.get()
                back = bpm['back'] # 뒤집
                fall = bpm['fall']  # 낙상
                night = bpm['night'] # 야간
                print("back : ", back, "fall : ", fall, "night : ", night)
                count = 0

                if night == 0 :
                    cap = cv2.VideoCapture(0)
                if night == 1 :
                    cap = cv2.VideoCapture(1)
                    
                cap.set(3,1280) # 가로 설정
                cap.set(4,720) # 세로 설정    

            # 필요에 따라 성능 향상을 위해 이미지 작성을 불가능함으로 기본 설정합니다.
            image.flags.writeable = False
            image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            results = pose.process(image)

            if not results.pose_landmarks:
                continue
            #print(
                #f'Nose coordinates: ('
                #f'{results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.RIGHT_SHOULDER].z}, '
            #)

            # 포즈 주석을 이미지 위에 그립니다.
            image.flags.writeable = True
            image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
            mp_drawing.draw_landmarks(
                image,
                results.pose_landmarks,
                mp_pose.POSE_CONNECTIONS,
                landmark_drawing_spec=mp_drawing_styles.get_default_pose_landmarks_style())
            
            if back == 1 :
                back_off = 1
                if isFlipped(image, results) :
                    print("Flipped")
                    if not once_flipped :
                        # 뒤집혀졌을 때 한 번만 알림이 가기위해 firebase 에서 값 변경
                        dir = db.reference('BED')
                        dir.update({'back': back})
                        once_flipped = True
                else :
                    if once_flipped == True :
                        once_flipped = False
            else :
                if back_off == 1 :
                    dir = db.reference('BED')
                    dir.update({'back': 0})
                    back_off += 1

            # 함수 호출
            drawPoly(image,polygon,(255,153,153)) # 사각형 그리기
            right = isInside(image,polygon,results.pose_landmarks.landmark[mp_pose.PoseLandmark.RIGHT_SHOULDER], results.pose_landmarks.landmark[mp_pose.PoseLandmark.RIGHT_SHOULDER])
            left = isInside(image,polygon,results.pose_landmarks.landmark[mp_pose.PoseLandmark.LEFT_SHOULDER], results.pose_landmarks.landmark[mp_pose.PoseLandmark.LEFT_SHOULDER])

            #print("Z : ",(results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.RIGHT_SHOULDER].z + results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.LEFT_SHOULDER].z) / 2)
            #print(fall_count)

            if fall == 1 :
                fall_off = 1
                if right : # 오른쪽이 위험 선을 넘어갔을 때
                    if (results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.RIGHT_SHOULDER].z + results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.LEFT_SHOULDER].z) / 2 > -0.18 :
                        fall_count += 1
                        if fall_count == 30:
                            print("fell off")
                            dir = db.reference('BED')
                            dir.update({'fall' : fall})

                    else :
                        print("not fell off")
                        fall_count = 0 

                if left : # 왼쪽이 위험 선을 넘어갔을 때
                    if (results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.LEFT_SHOULDER].z + results.pose_world_landmarks.landmark[mp_pose.PoseLandmark.RIGHT_SHOULDER].z) / 2 > -0.18 :
                        if fall_count == 30:
                            print("fell off")
                            dir = db.reference('BED')
                            dir.update({'fall' : fall})
                    else :                   
                        #print("not fell off") 
                        fall_count = 0
            else :
                if fall_off == 1 :
                    fall_count = 0
                    dir = db.reference('BED')
                    dir.update({'fall' : 0})
                    fall_off += 1

            # 코의 점을 기준으로 선을 하나 긋는다.
            nose_points = np.array([(results.pose_landmarks.landmark[mp_pose.PoseLandmark.NOSE].x * image.shape[1],results.pose_landmarks.landmark[mp_pose.PoseLandmark.NOSE].y * image.shape[0] - 40),(results.pose_landmarks.landmark[mp_pose.PoseLandmark.NOSE].x * image.shape[1],results.pose_landmarks.landmark[mp_pose.PoseLandmark.NOSE].y * image.shape[0] + 40)],np.int32)
            cv2.polylines(image,[nose_points],True,(255,0,0),5)

            #mp_drawing.plot_landmarks(results.pose_world_landmarks, mp_pose.POSE_CONNECTIONS)

            # 영상을 표시한다.
            cv2.imshow('MediaPipe Pose', image)
            if cv2.waitKey(5) & 0xFF == 27:
                dir = db.reference('BED')
                dir.update({'fall' : 0})      
                dir.update({'back' : 0})           
                break

    cap.release()
    cv2.destroyAllWindows()
if __name__ == "__main__" :
    main()