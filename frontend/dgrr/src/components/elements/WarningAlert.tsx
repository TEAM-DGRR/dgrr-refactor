import { warn } from "console";
import Swal from "sweetalert2";

const WarningAlert = Swal.mixin({
  // 나중에 아이콘 커스텀 예정
  icon: "warning",
  iconColor: "#EF665B",
  showCancelButton: true,
  showConfirmButton: true,
  confirmButtonText: "확인",
  cancelButtonText: "취소",
  width: "320px",
  heightAuto: true,
  confirmButtonColor: "#469FF6",
  cancelButtonColor: "#9B9B9B",
  didOpen(toast) {
    toast.addEventListener("mouseenter", Swal.stopTimer);
    toast.addEventListener("mouseleave", Swal.resumeTimer);
  },
});

export default WarningAlert;
