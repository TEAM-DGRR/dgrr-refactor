import Swal from 'sweetalert2';

const WarningAlert = async () => {
  const result = await Swal.fire({
    // 나중에 아이콘 커스텀 예정
    icon: 'warning',
    iconColor: '#EF665B',
    showCancelButton: true,
    showConfirmButton: true,
    confirmButtonText: '확인',
    cancelButtonText: '취소',
    width: '320px',
    heightAuto: true,
    confirmButtonColor: '#469FF6',
    cancelButtonColor: '#9B9B9B',
    text: '정말 나가시겠어요?',
  });

  if (result.isConfirmed) {
    return true;
  } else {
    return false;
  }
}
export default WarningAlert;
