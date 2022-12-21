import socket
import os
import numpy as np
import random
from game_rules import move_id2move_action, Game, Board, state_list2state_array
from mcts import MCTSPlayer
from Greedy import get_action
from config import CONFIG
os.chdir('python')


def read_file(ff):
    ff = ff.split('\n')
    color = '黑' if ff[0] == -1 else '红'
    scores = ff[1]
    ff = ff[2:-1]
    chessboard = [['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一'],
                   ['一一', '一一', '一一', '一一']]
    chess_list = ['帅','士','象','车','马','兵','炮']
    assert len(ff) == 32
    ff_index = -1
    hidden_chess = []
    for i in range(8):
        for j in range(4):
            ff_index += 1
            vals = ff[ff_index].split(' ')
            if vals[0] == 'true':
                chessboard[i][j] = '零零'
                continue
            if vals[3] == 'false':
                chess = '红' if vals[1] == 'RED' else '黑'
                hidden_chess.append(chess + chess_list[int(vals[2])-1])
                continue
            chessboard[i][j] = '红' if vals[1] == 'RED' else '黑'
            chessboard[i][j] += chess_list[int(vals[2])-1]
    return chessboard, color, scores, hidden_chess


def generate_file(ff, color, scores, move):
    ffout = ''
    ffout += '1' if color == '黑' else '-1'
    ffout += '\n'
    ffout += ' '.join(scores)
    ffout += '\n'
    ff = ff.split('\n')[2:]
    move = [int(i) for i in move]
    if move[:2] == move[2:]:
        ff[move[0]*4+move[1]] = ff[move[0]*4+move[1]].split(' ')
        ff[move[0] * 4 + move[1]][3] = 'true'
        ff[move[0] * 4 + move[1]] = ' '.join(ff[move[0] * 4 + move[1]])
        ffout += '\n'.join(ff)
        return ffout
    ff[move[0] * 4 + move[1]] = ff[move[0] * 4 + move[1]].split(' ')
    new_color = ff[move[0] * 4 + move[1]][1]
    new_chess = ff[move[0] * 4 + move[1]][2]
    ff[move[0] * 4 + move[1]] = 'true NONE 0 true false'
    ff[move[2] * 4 + move[3]] = ff[move[2] * 4 + move[3]].split(' ')
    ff[move[2] * 4 + move[3]][0] = 'false'
    ff[move[2] * 4 + move[3]][1] = new_color
    ff[move[2] * 4 + move[3]][2] = new_chess
    ff[move[2] * 4 + move[3]][2] = ' '.join(ff[move[2] * 4 + move[3]][2])
    ff[move[2] * 4 + move[3]] = ' '.join(ff[move[2] * 4 + move[3]])
    ffout += '\n'.join(ff)
    ffout+='\n'
    return ffout


def cal_scores(chessboard, move, color, scores):
    if move[:2] == move[2:]:
        return scores.split(' ')
    move = [int(i) for i in move]
    chess_scores = dict(帅=30, 士=10, 象=5, 车=5, 马=5, 兵=1, 炮=5)
    scores = scores.split(' ')
    index = 0 if color == '黑' else 1
    if chessboard[move[2]][move[3]][1] != '零' and chessboard[move[2]][move[3]][1] != '一':
        scores[index] = str(int(scores[index]) + chess_scores[chessboard[move[2]][move[3]][1]])
        print('scores',scores[index])
    return scores


def generate_move(recent_chessboard,color):
    if CONFIG['use_frame'] == 'pytorch':
        from pytorch_net import PolicyValueNet
        policy_value_net = PolicyValueNet(model_file='current_policy.pkl')
    else:
        print('暂不支持您选择的框架')
    mcts_player = MCTSPlayer(policy_value_net.policy_value_fn,
                             c_puct=5,
                             n_playout=100,
                             is_selfplay=0)
    num = random.choice([0,1])
    move = get_action(recent_chessboard,color) if num == 0 else mcts_player.get_action(recent_chessboard)
    return move



if __name__ == '__main__':
    s = socket.socket()  # 创建 socket 对象
    host = '127.0.0.1'  # 获取本地主机名
    #print(socket.gethostbyname(socket.gethostname()))
    port = 12345  # 设置端口
    s.bind((host, port))  # 绑定端口

    s.listen(5)  # 等待客户端连接
    while True:
        c, addr = s.accept()  # 建立客户端连接
        print('连接地址：', addr)
        #c.send('欢迎使用AI翻翻棋！'.encode(encoding="utf-8"))
        while True:
            fin = c.recv(1024).decode(encoding='utf-8')
            print('original_file')
            print(fin)

            chessboard, color,scores, hidden = read_file(fin)
            #chessboard = state_list2state_array(chessboard)
            board = Board( [chessboard] , color,hidden)
            move = move_id2move_action[generate_move(board,color)]
            scores = cal_scores(chessboard,move,color,scores)
            fout = generate_file(fin, color, scores, move)
            print('move:',move)
            c.send(fout.encode(encoding="utf-8"))
            print('moved_file')
            print(fout)
        c.close()

