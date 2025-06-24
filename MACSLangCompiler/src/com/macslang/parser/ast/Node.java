package com.macslang.parser.ast;

public interface Node {
<T> T accept(NodeVisitor<T> visitor);
}