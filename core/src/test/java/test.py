from passlib.hash import md5_crypt

def verify(word,crypt_word):
    result=[]
    for crypt_w in crypt_word:
        for w in word:
            if md5_crypt.verify(w, crypt_w):
                item = 'word:{}, crypt_word:{}'.format(w,crypt_w)
                result.append(item)
                break
    return result

