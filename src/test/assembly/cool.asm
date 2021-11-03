mov ab, 0
mov bb, 5
mov cb, 7
start:
add cb, 0
jez end
add ab, bb
sub cb, 1
jmp start
end:
jmp end
