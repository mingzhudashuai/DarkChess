import random
import copy
from collections import deque
import numpy as np
from game_rules import get_legal_moves,chess_scores, move_id2move_action, move_action2move_id

all_moves = []

def get_action(board_init, color):
    global all_moves
    board = copy.deepcopy(board_init.state_deque)
    move_list = get_legal_moves(board, color)
    score_list = {move_id2move_action[i]: 0 for i in move_list}
    chosen_list = []
    for i in move_list:
        i = move_id2move_action[i]
        if board[0][int(i[-2])][int(i[-1])] == '一一' and board[0][int(i[0])][int(i[1])][0] == '炮':
            chosen_list.append(i)
        score_list[i] = chess_scores[board[0][int(i[-2])][int(i[-1])][1]] if board[0][int(i[-2])][int(i[-1])] not in  ['一一', '零零'] else 0
    score_list = sorted(score_list.items(), key=lambda x:x[1])
    chosen_list.append(score_list[-1][0])
    for i in range(len(score_list)-2, 0):
        if score_list[i][-1] == score_list[-1][-1] and score_list[i][-1] not in all_moves:
            chosen_list.append(score_list[i])
        else:
            break
    choice = random.choice(chosen_list)
    all_moves.append(choice)
    if len(all_moves)>3:
        all_moves = all_moves[-3:]
    return move_action2move_id[choice]




