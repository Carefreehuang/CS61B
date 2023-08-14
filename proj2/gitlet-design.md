# Gitlet Design Document

**Name**: carefreehaung

---
## Classes and Data Structures

---
### Main

#### Fields

1. main 程序执行主体
2. validateNumArgs 判断参数是否对应 


### Commit

#### Fields

1. message -- commit 时的信息（string）
2. parentsID -- 上一次的提交（id形式）
3. date  -- 提交的日期
4. commitID -- 自身的ID
5. blobID -- 追踪文件内容，指向blob对象

### Blob

#### Fields

1. fileName
2. fileContent
3. blobID
## Algorithms

---


## Persistence

---
### git参考

### gitlet 实现




