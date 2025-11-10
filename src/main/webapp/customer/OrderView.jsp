<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.pttk.restman.model.Table" %>
<%@ page import="java.util.List" %>
<%
    String selectedDate = (String) request.getAttribute("selectedDate");
    String selectedSlot = (String) request.getAttribute("selectedSlot");
    String flashSuccess = (String) request.getAttribute("flash_success");
    String flashError   = (String) request.getAttribute("flash_error");
    List<Table> availableTables    = (List<Table>) request.getAttribute("availableTables");
    List<Table> tempSelectedTables = (List<Table>) request.getAttribute("tempSelectedTables");

    String sessDate = (String) session.getAttribute("tempDate");
    String sessSlot = (String) session.getAttribute("tempSlot");
    String effectiveDate = (selectedDate != null && !selectedDate.isEmpty())
            ? selectedDate
            : (sessDate != null ? sessDate : request.getParameter("date"));
    String effectiveSlot = (selectedSlot != null && !selectedSlot.isEmpty())
            ? selectedSlot
            : (sessSlot != null ? sessSlot : request.getParameter("slot"));

    String[] slots = new String[]{
            "07:00-09:00","09:00-11:00","11:00-13:00","13:00-15:00","17:00-19:00","19:00-21:00"
    };
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Order View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
        :root{
            --primary:#1976d2;         /* xanh dương */
            --primary-hover:#1565c0;
            --danger:#b91c1c;
            --muted:#6b7280;
            --border:#e5e7eb;
            --bg:#f8fafc;
        }
        body { font-family: system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif; margin:24px; background:var(--bg);}
        h1 { margin: 0 0 12px; }
        .card { border:1px solid var(--border); border-radius:12px; padding:16px; box-shadow:0 1px 2px rgba(0,0,0,.04); margin-bottom:16px; background:#fff; }
        .muted { color:var(--muted); font-size:14px; }
        .row { display:flex; gap:12px; flex-wrap:wrap; align-items:flex-end; }
        .col { display:flex; flex-direction:column; gap:6px; }
        input, select, button { padding:10px 12px; border:1px solid #d1d5db; border-radius:8px; font-size:14px; }
        input, select { background:#fff; }
        .btn { cursor:pointer; border:none; color:#fff; background:var(--primary); box-shadow:0 6px 16px rgba(25,118,210,.25); }
        .btn:hover { background:var(--primary-hover); }
        .btn-outline { background:#fff; color:#111827; border:1px solid #d1d5db; box-shadow:none; }
        .btn-outline:hover { background:#f3f4f6; }
        .btn-danger { background:var(--danger); box-shadow:0 6px 16px rgba(185,28,28,.2); }
        table { width:100%; border-collapse:collapse; margin-top:12px; }
        th, td { padding:10px 12px; border-bottom:1px solid #f3f4f6; text-align:left; }
        .success { background:#ecfdf5; color:#065f46; padding:8px 10px; border-radius:8px; }
        .error { background:#fef2f2; color:#991b1b; padding:8px 10px; border-radius:8px; }
        .pill { padding:6px 10px; border-radius:9999px; background:#f3f4f6; font-size:12px; margin-right:6px; display:inline-block; }
        .actions { display:flex; gap:8px; align-items:center; flex-wrap:wrap; }
        .grid { display:grid; grid-template-columns: repeat(auto-fit, minmax(220px,1fr)); gap:12px; }
        label { display:block; font-size:13px; color:#374151; }
        .toolbar { display:flex; gap:12px; align-items:flex-end; flex-wrap:wrap; }
        .toolbar > form { display:flex; gap:12px; align-items:flex-end; flex-wrap:wrap; }
    </style>
    <script>
        function confirmCommit(count, date, slot) {
            if (count <= 0) {
                alert("Chưa chọn bàn nào.");
                return false;
            }
            if (!date || !slot) {
                alert("Vui lòng chọn ngày và khung giờ trước.");
                return false;
            }
            return confirm(`Xác nhận ĐẶT ${count} bàn cho ${date} - ${slot}?`);
        }
    </script>
</head>
<body>
<h1>Đặt bàn online</h1>

<% if (flashSuccess != null) { %><div class="success"><%= flashSuccess %></div><% } %>
<% if (flashError   != null) { %><div class="error"><%= flashError %></div><% } %>

<div class="card">
    <!-- Thanh công cụ: 2 form tách biệt, KHÔNG lồng nhau -->
    <div class="toolbar">
        <!-- Form xem bàn trống (GET) -->
        <form method="get" action="<%= request.getContextPath() %>/TableReservationServlet">
            <div class="col">
                <label>Chọn ngày</label>
                <input type="date" name="date" value="<%= (effectiveDate != null ? effectiveDate : "") %>" required />
            </div>
            <div class="col">
                <label>Khung giờ (2 tiếng)</label>
                <select name="slot" required>
                    <option value="" disabled <%= (effectiveSlot == null) ? "selected" : "" %> >--Chọn khung giờ--</option>
                    <%
                        for (String s : slots) {
                    %>
                    <option value="<%= s %>" <%= (s.equals(effectiveSlot)) ? "selected" : "" %>><%= s %></option>
                    <% } %>
                </select>
            </div>
            <button class="btn" type="submit">Xem bàn trống</button>
        </form>

        <!-- Form xoá danh sách tạm (POST) -->
        <form method="post" action="<%= request.getContextPath() %>/TableReservationServlet">
            <input type="hidden" name="action" value="clearTemp" />
            <button class="btn btn-outline" type="submit">Xoá danh sách tạm</button>
        </form>
    </div>

    <div class="muted" style="margin-top:8px">
        <% if (effectiveDate != null && effectiveSlot != null) { %>
        <span class="pill">Ngày: <%= effectiveDate %></span>
        <span class="pill">Khung: <%= effectiveSlot %></span>
        <% } %>
    </div>
</div>

<div class="card">
    <h2 style="margin:0;">Bàn đã chọn (tạm)</h2>
    <%
        int tempCount = (tempSelectedTables == null) ? 0 : tempSelectedTables.size();
        if (tempCount == 0) {
    %>
    <p class="muted">Chưa có bàn nào trong danh sách tạm.</p>
    <% } else { %>
    <table>
        <thead><tr><th>#</th><th>Số bàn</th><th>Hành động</th></tr></thead>
        <tbody>
        <%
            for (int i = 0; i < tempSelectedTables.size(); i++) {
                Table tt = tempSelectedTables.get(i);
        %>
        <tr>
            <td><%= (i+1) %></td>
            <td><%= tt.getNumberTable() %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/TableReservationServlet" style="display:inline;">
                    <input type="hidden" name="action" value="removeTemp" />
                    <input type="hidden" name="tableId" value="<%= tt.getId() %>" />
                    <button class="btn btn-danger" type="submit">Bỏ chọn</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>

<form class="card" method="post" action="<%= request.getContextPath() %>/TableReservationServlet"
      onsubmit="return confirmCommit(<%= tempCount %>, '<%= (effectiveDate!=null?effectiveDate:"") %>', '<%= (effectiveSlot!=null?effectiveSlot:"") %>')">
    <input type="hidden" name="action" value="commitReservation" />
    <input type="hidden" name="date"   value="<%= (effectiveDate!=null?effectiveDate:"") %>" />
    <input type="hidden" name="slot"   value="<%= (effectiveSlot!=null?effectiveSlot:"") %>" />

    <h2 style="margin-top:0;">Thông tin khách hàng</h2>
    <p class="muted">Nếu đã đăng nhập, có thể bỏ qua phần này. Nếu chưa, vui lòng nhập tối thiểu Họ tên và Số điện thoại.</p>

    <div class="grid">
        <div>
            <label>Họ tên</label>
            <input type="text" name="customerName" placeholder="Nguyễn Văn A" />
        </div>
        <div>
            <label>Số điện thoại</label>
            <input type="tel" name="customerPhone" placeholder="09xxxxxxxx" />
        </div>
        <div>
            <label>Email (tuỳ chọn)</label>
            <input type="email" name="customerEmail" placeholder="email@domain.com" />
        </div>
    </div>

    <div style="margin-top:12px;">
        <button class="btn" type="submit">ĐẶT</button>
    </div>
</form>

<% if (availableTables != null && !availableTables.isEmpty()) { %>
<div class="card">
    <h2 style="margin:0;">Bàn trống (<%= availableTables.size() %> bàn)</h2>
    <table>
        <thead><tr><th>#</th><th>Số bàn</th><th>Hành động</th></tr></thead>
        <tbody>
        <%
            for (int i = 0; i < availableTables.size(); i++) {
                Table t = availableTables.get(i);
        %>
        <tr>
            <td><%= (i + 1) %></td>
            <td><%= t.getNumberTable() %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/TableReservationServlet">
                    <input type="hidden" name="action" value="addTemp" />
                    <input type="hidden" name="tableId" value="<%= t.getId() %>" />
                    <input type="hidden" name="date" value="<%= (effectiveDate!=null?effectiveDate:"") %>" />
                    <input type="hidden" name="slot" value="<%= (effectiveSlot!=null?effectiveSlot:"") %>" />
                    <button class="btn" type="submit">Chọn tạm</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
<% } %>

</body>
</html>
