import numpy as np
import copy
import time
import random
from collections import deque
from config import CONFIG


#列表表示棋盘    一一表示未翻 零零表示空 红/黑表示对应棋子
state_list_init = [['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一']]

# deque来存储棋盘状态，长度为4
state_deque_init = deque(maxlen=4)
for _ in range(4):
    state_deque_init.append(copy.deepcopy(state_list_init))

#列表表示初始棋盘未翻开部分
hidden_chess_init = ['红车','红马','红象','红士','红炮'] *2 + ['红帅'] + ['红兵'] * 5 +\
                    ['黑车','黑马','黑象','黑士','黑炮'] *2 + ['黑帅'] + ['黑兵'] * 5


# 构建字典表示吃子的分数
chess_scores = dict(帅=30, 士=10, 象=5, 车=5, 马 =5, 兵=1, 炮=5)

# 构建一个字典：字符串到数组的映射，函数：数组到字符串的映射
string2array = dict(红车=np.array([1, 0, 0, 0, 0, 0, 0, 0]), 红马=np.array([0, 1, 0, 0, 0, 0, 0, 0]),
                    红象=np.array([0, 0, 1, 0, 0, 0, 0, 0]), 红士=np.array([0, 0, 0, 1, 0, 0, 0, 0]),
                    红帅=np.array([0, 0, 0, 0, 1, 0, 0, 0]), 红炮=np.array([0, 0, 0, 0, 0, 1, 0, 0]),
                    红兵=np.array([0, 0, 0, 0, 0, 0, 1, 0]), 黑车=np.array([-1, 0, 0, 0, 0, 0, 0, 0]),
                    黑马=np.array([0, -1, 0, 0, 0, 0, 0, 0]), 黑象=np.array([0, 0, -1, 0, 0, 0, 0, 0]),
                    黑士=np.array([0, 0, 0, -1, 0, 0, 0, 0]), 黑帅=np.array([0, 0, 0, 0, -1, 0, 0, 0]),
                    黑炮=np.array([0, 0, 0, 0, 0, -1, 0, 0]), 黑兵=np.array([0, 0, 0, 0, 0, 0, -1, 0]),
                    一一=np.array([0, 0, 0, 0, 0, 0, 0, 1]), 零零=np.array([0, 0, 0, 0, 0, 0, 0, 0]))

def array2string(array):
    return list(filter(lambda string: (string2array[string] == array).all(), string2array))[0]


# 改变棋盘状态
def change_state(state_list, move, hidden_chess, all_score):
    """move : 字符串'0010'"""
    copy_list = copy.deepcopy(state_list)
    y, x, toy, tox = int(move[0]), int(move[1]), int(move[2]), int(move[3])
    if y == toy and x == tox:
        #if copy_list[y][x] == '一一':
        all_score.append(0)
        copy_list[toy][tox] = random.choice(hidden_chess)
        if len(all_score) > 1:
            hidden_chess.remove(copy_list[toy][tox])
        return copy_list
    else:
        if copy_list[toy][tox] not in  ['零零','一一']:
            all_score.append(chess_scores[copy_list[toy][tox][-1]])
        else:
            all_score.append(0)
        copy_list[toy][tox] = copy_list[y][x]
        copy_list[y][x] = '零零'
        return copy_list
        '''if copy_list[y][x] not in ['零零','一一']:
            if copy_list[toy][tox] == '零零':
                copy_list[toy][tox] = copy_list[y][x]
                copy_list[y][x] = '零零'
                return copy_list
            
            if chess_scores[copy_list[y][x][1]] >= chess_scores[copy_list[toy][tox][1]]:
                copy_list[toy][tox] = copy_list[y][x]
                copy_list[y][x] = '零零'
                return copy_list
            
            if chess_scores[copy_list[y][x][1]] == 1 and chess_scores[copy_list[toy][tox][1]] == 30:
                copy_list[toy][tox] = copy_list[y][x]
                copy_list[y][x] = '零零'
                return copy_list'''
        



# 列表棋盘状态到数组棋盘状态
def state_list2state_array(state_list):
    _state_array = np.zeros([8, 4, 8])   # 10是8种棋子种类+1先后手+1对手上一步特征
    for i in range(8):
        for j in range(4):
            _state_array[i][j] = string2array[state_list[i][j]]
    return _state_array


# 拿到所有合法走子的集合，2086长度，也就是神经网络预测的走子概率向量的长度
# 第一个字典：move_id到move_action
# 第二个字典：move_action到move_id
# 例如：move_id:0 --> move_action:'0010'
def get_all_legal_moves():
    _move_id2move_action = {}
    _move_action2move_id = {}
    row = ['0', '1', '2', '3']
    column = ['0', '1', '2', '3', '4', '5', '6', '7']


    idx = 0
    for l1 in range(8):
        for n1 in range(4):
            destinations = [(t, n1) for t in range(8)] + \
                           [(l1, t) for t in range(4)]  # 炮可以任意吃子 其他棋子只可以上下左右移动一格
            for (l2, n2) in destinations:
                if (l1, n1) != (l2, n2) and l2 in range(8) and n2 in range(4):    # 当l1=l2 n1=n2的时候就是翻棋的意思
                    action = column[l1] + row[n1] + column[l2] + row[n2]
                    _move_id2move_action[idx] = action
                    _move_action2move_id[action] = idx
                    idx += 1
            action = column[l1] + row[n1] + column[l1] + row[n1]
            _move_id2move_action[idx] = action
            _move_action2move_id[action] = idx
            idx += 1

    return _move_id2move_action, _move_action2move_id


move_id2move_action, move_action2move_id = get_all_legal_moves() # 移动棋子一共只有352种可能

#print(move_id2move_action)
# 走子翻转的函数，用来扩充我们的数据
def flip_map(string):
    new_str = ''
    for index in range(4):
        if index == 0 or index == 2:
            new_str += (str(7- int(string[index])))
        else:
            new_str += (str(int(string[index])))
    return new_str

# 边界检查
def check_bounds(toY, toX):
    if toY in [0, 1, 2, 3, 4, 5, 6, 7] and toX in [0, 1, 2, 3]:
        return True
    return False


# 不能走到自己的棋子位置
def check_obstruct(piece, current_player_color):
    # 当走到的位置存在棋子的时候，进行一次判断
    if piece != '一一' and piece != '零零':
        if current_player_color == '红':
            if '黑' in piece:
                return True
            else:
                return False
        elif current_player_color == '黑':
            if '红' in piece:
                return True
            else:
                return False
    else:
        return True


# 分数高的棋子才能吃分数低的棋子
def check_score(departure_piece, destination_piece):
    if destination_piece == '一一':
        return False
    if destination_piece == '零零':
        return True
    if departure_piece[1] == '兵' and destination_piece[1] == '帅':
        return True
    if departure_piece[1] == '帅' and destination_piece[1] == '帅':
        return False
    if departure_piece[1] == '帅' and destination_piece[1] == '兵':
        return False
    return True if chess_scores[departure_piece[1]] >= chess_scores[destination_piece[1]] else False
    


# 得到当前盘面合法走子集合
# 输入状态队列不能小于10，current_player_color:当前玩家控制的棋子颜色
# 用来存放合法走子的列表
def get_legal_moves(state_deque, current_player_color):

    state_list = state_deque[-1]


    moves = []  # 用来存放所有合法的走子方法

    # state_list是以列表形式表示的, len(state_list) == 8, len(state_list[0]) == 4
    # 遍历移动初始位置
    for y in range(8):
        for x in range(4):
            # 只有是棋子才可以移动
            if state_list[y][x] == '零零':
                pass
            else:
                if state_list[y][x] == '一一':  # 未翻棋子的合法走子
                    toY = y
                    toX = x
                    # 未翻棋子只能翻过来 位置不变
                    m = str(y) + str(x) + str(toY) + str(toX)
                    moves.append(m)

                elif state_list[y][x] == '黑炮' and current_player_color == '黑':  # 黑炮的合法走子
                    toY = y
                    hits = False
                    for toX in range(x - 1, -1, -1):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '红' in state_list[toY][toX]:  # 黑炮可以吃未翻棋子和红棋
                                moves.append(m)
                                break
                    hits = False
                    for toX in range(x + 1, 4):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '红' in state_list[toY][toX]:
                                moves.append(m)
                                break
                    
                    toX = x
                    hits = False
                    for toY in range(y - 1, -1, -1):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '红' in state_list[toY][toX]:
                                moves.append(m)
                                break
                    hits = False
                    for toY in range(y + 1, 8):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '红' in state_list[toY][toX]:
                                moves.append(m)
                                break
                
                elif state_list[y][x] == '红炮' and current_player_color == '红':  # 红炮的合法走子
                    toY = y
                    hits = False
                    for toX in range(x - 1, -1, -1):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '黑' in state_list[toY][toX]:
                                moves.append(m)
                                break
                    hits = False
                    for toX in range(x + 1, 4):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '黑' in state_list[toY][toX]:
                                moves.append(m)
                                break

                    toX = x
                    hits = False
                    for toY in range(y - 1, -1, -1):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '黑' in state_list[toY][toX]:
                                moves.append(m)
                                break
                    hits = False
                    for toY in range(y + 1, 8):
                        m = str(y) + str(x) + str(toY) + str(toX)
                        if hits is False:
                            if state_list[toY][toX] != '零零':
                                hits = True
                        else:
                            if state_list[toY][toX] == '一一' or '黑' in state_list[toY][toX]:
                                moves.append(m)
                                break

                # 常规黑棋子的合法走子
                elif '黑' in state_list[y][x] and current_player_color == '黑':
                    destination = [(-1, 0), (1, 0), (0, -1), (0, 1)]
                    for (l,n) in destination:
                        toY = y + l
                        toX = x + n
                        if check_bounds(toY, toX) and check_obstruct(state_list[toY][toX], current_player_color='黑') \
                            and check_score(state_list[y][x], state_list[toY][toX]):
                            m = str(y) + str(x) + str(toY) + str(toX)
                            moves.append(m)
                    
                # 红兵的合法走子
                elif '红' in state_list[y][x] and current_player_color == '红':
                    destination = [(-1, 0), (1, 0), (0, -1), (0, 1)]
                    for (l,n) in destination:
                        toY = y + l
                        toX = x + n
                        if check_bounds(toY, toX) and check_obstruct(state_list[toY][toX], current_player_color='红') \
                            and check_score(state_list[y][x], state_list[toY][toX]):
                            m = str(y) + str(x) + str(toY) + str(toX)
                            moves.append(m)

    moves_id = []
    for move in moves:
        moves_id.append(move_action2move_id[move])
    return moves_id


# 棋盘逻辑控制
class Board(object):

    def __init__(self, board=state_list_init, color=0, hiddenchess=hidden_chess_init):
        self.state_list = copy.deepcopy(board)
        self.game_start = False
        self.winner = None
        self.state_deque = copy.deepcopy(board)
        self.hidden_chess = copy.deepcopy(hiddenchess)
        self.first_chess = None
        self.scores = []
        self.color = color
        self.action_count = -1
        self.kill_action = 0

    # 初始化棋盘的方法
    def init_board(self, start_player=1):  
        self.start_player = start_player
        self.hidden_chess = copy.deepcopy(hidden_chess_init)
        first_chess = random.choice(self.hidden_chess)
        first_color = first_chess[0]
        self.first_chess = first_chess
        second_color = '红' if first_color == '黑' else '黑'
        print(first_chess)
        print(self.hidden_chess)
        self.hidden_chess.remove(first_chess)

        if start_player == 1:
            self.id2color = {1: first_color, 2: second_color}
            self.color2id = {first_color: 1, second_color: 2}
            self.backhand_player = 2
        elif start_player == 2:
            self.id2color = {2: first_color, 1: second_color}
            self.color2id = {first_color: 2, second_color: 1}
            self.backhand_player = 1
        # 当前手玩家，也就是先手玩家
        self.current_player_color = self.id2color[start_player]     # 红
        self.current_player_id = self.color2id[first_color]
        # 初始化棋盘状态
        self.state_list = copy.deepcopy(state_list_init)
        self.state_deque = copy.deepcopy(state_deque_init)
        # 初始化最后落子位置
        self.last_move = -1
        # 记录游戏中吃子的回合数
        self.kill_action = 0
        self.game_start = False
        self.action_count = 0   # 游戏动作计数器
        self.winner = None

    @property
    # 获的当前盘面的所有合法走子集合
    def availables(self):
        if self.color == 0:
            self.color = self.current_player_color
        return get_legal_moves(self.state_deque, self.color)

    # 从当前玩家的视角返回棋盘状态，current_state_array: [10, 8, 4]  CHW
    def current_state(self):
        _current_state = np.zeros([10, 8, 4])
        # 使用10个平面来表示棋盘状态
        # 0-7个平面表示棋子位置，1代表红方棋子，-1代表黑方棋子, np.inf代表未翻棋子，队列最后一个盘面
        # 第8个平面表示对手player最近一步的落子位置，走子之前的位置为-1，走子之后的位置为1，其余全部是0
        # 第9个平面表示的是当前player是不是先手player，如果是先手player则整个平面全部为1，否则全部为0
        _current_state[:8] = state_list2state_array(self.state_deque[-1]).transpose([2, 0, 1])  # [8, 8, 4]

        if self.game_start:
            # 解构self.last_move
            move = move_id2move_action[self.last_move]
            start_position = int(move[0]), int(move[1])
            end_position = int(move[2]), int(move[3])
            _current_state[8][start_position[0]][start_position[1]] = -1
            _current_state[8][end_position[0]][end_position[1]] = 1
        # 指出当前是哪个玩家走子
        if self.action_count == -1:
            _current_state[9][:, :] = 1.0 if self.color == 1 else 0.0
        else:
            if self.action_count % 2 == 0:
                _current_state[9][:, :] = 1.0

        return _current_state

    # 根据move对棋盘状态做出改变
    def do_move(self, move):
        self.game_start = True  # 游戏开始
        self.action_count += 1  # 移动次数加1
        if self.color != 0:
            self.current_player_color = self.color
            self.current_player_id = 0
        move_action = move_id2move_action[move]
        start_y, start_x = int(move_action[0]), int(move_action[1])
        end_y, end_x = int(move_action[2]), int(move_action[3])
        if self.action_count == 1 and self.color == 0:
            assert [start_y, start_x] == [end_y, end_x]
            state_list = copy.deepcopy(self.state_deque[-1])
            state_list[end_y][end_x] = self.first_chess
            self.current_player_color = '黑' if self.current_player_color == '红' else '红'  # 改变当前玩家
            self.current_player_id = 1 if self.current_player_id == 2 else 2
            # 记录最后一次移动的位置
            self.last_move = move
            self.state_deque.append(state_list)
        else:
            state_list = copy.deepcopy(self.state_deque[-1])
            # 判断是否吃子 长时间不吃子则判结局
            if state_list[end_y][end_x] != '零零':
                if state_list[end_y][end_x] != '一一':
                    self.scores.append(chess_scores[state_list[end_y][end_x][1]])
                    # 更改棋盘状态
                    state_list[end_y][end_x] = state_list[start_y][start_x]
                    state_list[start_y][start_x] = '零零'
                else:
                    self.scores.append(0)
                    state_list[end_y][end_x] = random.choice(self.hidden_chess)
                    self.hidden_chess.remove(state_list[end_y][end_x])
                self.kill_action = 0
                if len(self.scores)%2 == 1:
                    scores_tmp = [self.scores[2*k] for k in range(len(self.scores)//2+1)]
                else:
                    scores_tmp = [self.scores[2*k+1] for k in range(len(self.scores)//2)]
                if self.current_player_color == '黑' and sum(scores_tmp)>=60:
                    self.winner = self.color2id['黑']
                elif self.current_player_color == '红'and sum(scores_tmp) >= 60:
                    self.winner = self.color2id['红']
            else:
                self.scores.append(0)
                self.kill_action += 1

                # 更改棋盘状态
                state_list[end_y][end_x] = state_list[start_y][start_x]
                state_list[start_y][start_x] = '零零'
            self.current_player_color = '黑' if self.current_player_color == '红' else '红'  # 改变当前玩家
            self.current_player_id = 1 if self.current_player_id == 2 else 2
            # 记录最后一次移动的位置
            self.last_move = move
            self.state_deque.append(state_list)

    # 是否产生赢家
    def has_a_winner(self):
        """一共有三种状态，红方胜，黑方胜，平局"""
        if self.winner is not None:
            return True, self.winner
        elif self.kill_action >= CONFIG['kill_action'] and len(self.scores)%2 == 0:  # 平局先手判负
            # return False, -1
            # 这里要补充判断分数
            scores_tmp_1 = [self.scores[2*k] for k in range(len(self.scores)//2)] 
            scores_tmp_2 = [self.scores[2*k+1] for k in range(len(self.scores)//2)]
            if sum(scores_tmp_1) > sum(scores_tmp_2):
                return True, 1
            if sum(scores_tmp_1) < sum(scores_tmp_2):
                return True, 2
            return False, -1
        return False, -1

    # 检查当前棋局是否结束
    def game_end(self):
        win, winner = self.has_a_winner()
        if win:
            return True, winner
        elif self.kill_action >= CONFIG['kill_action']:  # 平局，没有赢家
            return True, -1
        # 判断是否为死局
        if self.availables == []:
            if len(self.scores)%2 == 1:
                scores_tmp_1 = [self.scores[2*k] for k in range(len(self.scores)//2+1)]
                scores_tmp_2 = [self.scores[2*k+1] for k in range(len(self.scores)//2)]
            else:
                scores_tmp_1 = [self.scores[2*k] for k in range(len(self.scores)//2)]
                scores_tmp_2 = [self.scores[2*k+1] for k in range(len(self.scores)//2)]
            if sum(scores_tmp_1) >= sum(scores_tmp_2):
                return True, 1
            if sum(scores_tmp_1) < sum(scores_tmp_2):
                return True, 2
            return True, -1

        return False, -1

    def get_current_player_color(self):
        return self.current_player_color

    def get_current_player_id(self):
        return self.current_player_id


# 在Board类基础上定义Game类，该类用于启动并控制一整局对局的完整流程，并收集对局过程中的数据，以及进行棋盘的展示
class Game(object):

    def __init__(self, board):
        self.board = board

    # 可视化
    def graphic(self, board, player1_color, player2_color):
        print('player1 take: ', player1_color)
        print('player2 take: ', player2_color)

    # 用于人机对战，人人对战等
    def start_play(self, player1, player2, start_player=1, is_shown=1):
        if start_player not in (1, 2):
            raise Exception('start_player should be either 1 (player1 first) '
                            'or 2 (player2 first)')
        self.board.init_board(start_player)  # 初始化棋盘
        p1, p2 = 1, 2
        player1.set_player_ind(1)
        player2.set_player_ind(2)
        players = {p1: player1, p2: player2}
        if is_shown:
            self.graphic(self.board, player1.player, player2.player)

        while True:
            current_player = self.board.get_current_player_id()  # 红子对应的玩家id
            player_in_turn = players[current_player]  # 决定当前玩家的代理
            move = player_in_turn.get_action(self.board)  # 当前玩家代理拿到动作
            self.board.do_move(move)  # 棋盘做出改变
            if is_shown:
                self.graphic(self.board, player1.player, player2.player)
            end, winner = self.board.game_end()
            if end:
                if winner != -1:
                    print("Game end. Winner is", players[winner])
                else:
                    print("Game end. Tie")
                return winner

    def continue_play(self, player1, player2, board, start_player=1, is_shown=1):

        p1, p2 = 1, 2
        player1.set_player_ind(1)
        player2.set_player_ind(2)
        players = {p1: player1, p2: player2}
        if is_shown:
            self.graphic(self.board, player1.player, player2.player)

        while True:
            current_player = self.board.get_current_player_id()  # 红子对应的玩家id
            player_in_turn = players[current_player]  # 决定当前玩家的代理
            move = player_in_turn.get_action(board)  # 当前玩家代理拿到动作
            self.board.do_move(move)  # 棋盘做出改变
            if is_shown:
                self.graphic(self.board, player1.player, player2.player)
            end, winner = self.board.game_end()
            if end:
                if winner != -1:
                    print("Game end. Winner is", players[winner])
                else:
                    print("Game end. Tie")
                return winner

    # 使用蒙特卡洛树搜索开始自我对弈，存储游戏状态（状态，蒙特卡洛落子概率，胜负手）三元组用于神经网络训练
    def start_self_play(self, player, is_shown=False, temp=1e-3):
        self.board.init_board()     # 初始化棋盘, start_player=1
        p1, p2 = 1, 2
        states, mcts_probs, current_players = [], [], []
        # 开始自我对弈
        _count = 0
        while True:
            _count += 1
            if _count % 20 == 0:
                start_time = time.time()
                move, move_probs = player.get_action(self.board,
                                                     temp=temp,
                                                     return_prob=1)
                print('走一步要花: ', time.time() - start_time)
            else:
                move, move_probs = player.get_action(self.board,
                                                     temp=temp,
                                                     return_prob=1)
            # 保存自我对弈的数据
            states.append(self.board.current_state())
            mcts_probs.append(move_probs)
            current_players.append(self.board.current_player_id)
            # 执行一步落子
            self.board.do_move(move)
            end, winner = self.board.game_end()
            if end:
                # 从每一个状态state对应的玩家的视角保存胜负信息
                winner_z = np.zeros(len(current_players))
                if winner != -1:
                    winner_z[np.array(current_players) == winner] = 1.0
                    winner_z[np.array(current_players) != winner] = -1.0
                # 重置蒙特卡洛根节点
                player.reset_player()
                if is_shown:
                    if winner != -1:
                        print("Game end. Winner is:", winner)
                    else:
                        print('Game end. Tie')

                return winner, zip(states, mcts_probs, winner_z)


if __name__ == '__main__':
    # 测试array2string
    # _array = np.array([0, 0, 0, 0, 0, 0, 0])
    # print(array2num(_array))

    """# 测试change_state
    new_state = change_state(state_list_init, move='0010')
    for row in range(10):
        print(new_state[row])"""

    """# 测试get_legal_moves
    moves = get_legal_moves(state_deque_init, current_player_color='黑')
    move_actions = []
    for item in moves:
        move_actions.append(move_id2move_action[item])
    print(move_actions)"""

