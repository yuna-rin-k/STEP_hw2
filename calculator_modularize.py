def readNumber(line, index):
  number = 0
  while index < len(line) and line[index].isdigit():
    number = number * 10 + int(line[index])
    index += 1
  if index < len(line) and line[index] == '.':
    index += 1
    keta = 0.1
    while index < len(line) and line[index].isdigit():
      number += int(line[index]) * keta
      keta /= 10
      index += 1
  token = {'type': 'NUMBER', 'number': number}
  return token, index


def readPlus(line, index):
  token = {'type': 'PLUS'}
  return token, index + 1

def readMinus(line, index):
  token = {'type': 'MINUS'}
  return token, index + 1

def readMult(line, index):
  token = {'type': 'MULT'}
  return token, index + 1

def readDiv(line, index):
  token = {'type': 'DIV'}
  return token, index + 1

def tokenize(line):
  tokens = []
  index = 0
  while index < len(line):
    if line[index].isdigit():
      (token, index) = readNumber(line, index)
    elif line[index] == '+':
      (token, index) = readPlus(line, index)
    elif line[index] == '-':
      (token, index) = readMinus(line, index)
    elif line[index] == '*':
      (token, index) = readMult(line, index)
    elif line[index] == '/':
      (token, index) = readDiv(line, index)
    else:
      print('Invalid character found: ' + line[index])
      exit(1)
    tokens.append(token)
  return tokens


def evaluate(tokens):
  answer = 0
  tokens.insert(0, {'type': 'PLUS'}) # Insert a dummy '+' token
  index = 1

  while index < len(tokens):
    temp_result = 0
    op = tokens[index-1]['type']
    #先に *, /　の処理
    if tokens[index]['type'] == 'NUMBER' and op != 'PLUS' and op != 'MINUS':
      y = tokens.pop(index)
      op = tokens.pop(index-1)
      x = tokens.pop(index-2)
      if x['type'] != 'NUMBER' or y['type'] != 'NUMBER':
        print('Wrong grammer') 
        exit(1)
      y = y['number']
      x = x['number']
      op = op['type']
      if op == 'MULT':
        temp_result = x * y
        token = {'type': 'NUMBER', 'number': temp_result}
        index = index-2
        tokens.insert(index, token)
      elif op == 'DIV':
        temp_result = x / y
        token = {'type': 'NUMBER', 'number': temp_result}
        index = index-2
        tokens.insert(index, token)
    index += 1
  index = 1
  while index < len(tokens):
    if tokens[index]['type'] == 'NUMBER':
      if tokens[index - 1]['type'] == 'PLUS':
        answer += tokens[index]['number']
      elif tokens[index - 1]['type'] == 'MINUS':
        answer -= tokens[index]['number']
      else:
        print('Invalid syntax2')
        exit(1)
      if index-2 >= 0 and tokens[index-2]['type'] != 'NUMBER':
        print("Wrong grammer2")
        exit(1)
    index += 1
  return answer


def test(line):
  tokens = tokenize(line)
  actualAnswer = evaluate(tokens)
  expectedAnswer = eval(line)
  if abs(actualAnswer - expectedAnswer) < 1e-8:
    print("PASS! (%s = %f)" % (line, expectedAnswer))
  else:
    print("FAIL! (%s should be %f but was %f)" % (line, expectedAnswer, actualAnswer))


# Add more tests to this function :)
def runTest():
  print("==== Test started! ====")
  #op → +, -だけ
  #整数
  #整数と小数 + 項が2つより多いとき
  #答えがマイナス
  test("1+2") 
  test("1.0-2.534+300")
  test("23-500+4.3-9.0") 

  #op　→ *
  #基本
  #整数と小数 + 項が2つより多いとき
  #答えマイナス
  test("2*3")
  test("2*2*2.3")
  test("2*3*1.2")

  #op　→ /
  #基本
  #整数と小数 + 項が2つより多いとき
  #答えマイナス
  #- * - →　+
  test("9/3")
  test("4.5/3/15/2.0")
  #test("4.5/3/15/-2.0")
  #test("-4.5/3/15/-2.0")

  #op → +, -, * ,/
  #基本
  #小数有り + マイナス
  #マイナス*マイナスを含む
  test("1+4*4+1/2")
  #test("3.4*1.2/2-43+2")
  #test("-4.3/-1.4+1-1*2.0")

  #エラー
  #test("1++2")
  test("1*2//2")
  print("==== Test finished! ====\n")

runTest()

while True:
  print('> ', end="")
  line = input()
  tokens = tokenize(line)
  answer = evaluate(tokens)
  print("answer = %f\n" % answer)