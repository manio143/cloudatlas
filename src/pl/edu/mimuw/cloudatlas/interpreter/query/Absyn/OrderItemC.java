package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class OrderItemC extends OrderItem {
  public final CondExpr condexpr_;
  public final Order order_;
  public final Nulls nulls_;

  public OrderItemC(CondExpr p1, Order p2, Nulls p3) { condexpr_ = p1; order_ = p2; nulls_ = p3; }

  public <R,A> R accept(pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.OrderItem.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.OrderItemC) {
      pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.OrderItemC x = (pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.OrderItemC)o;
      return this.condexpr_.equals(x.condexpr_) && this.order_.equals(x.order_) && this.nulls_.equals(x.nulls_);
    }
    return false;
  }

  public int hashCode() {
    return 37*(37*(this.condexpr_.hashCode())+this.order_.hashCode())+this.nulls_.hashCode();
  }


}
